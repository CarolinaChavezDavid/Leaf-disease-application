package com.leafdisease.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.Environment
import com.google.ai.edge.litert.TensorBuffer
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.domain.model.PlantDiseaseInfo
import java.io.IOException
import kotlin.math.abs
import kotlin.math.exp

/**
 * Clasificador basado en EfficientNetV2B0 FP16.
 *
 * PREPROCESSING — diferencia crítica respecto a MobileNetV2:
 *   EfficientNetV2 fue entrenado con `include_preprocessing=False` y normalización
 *   `pixel / 255.0` en el pipeline Python. La entrada esperada es [0, 1].
 *
 *   MobileNetV2 espera [-1, 1] → formula: `(pixel - 127.5f) / 127.5f`
 *   EfficientNetV2 espera [0, 1] → formula: `pixel / 255.0f`
 *
 *   Usar la normalización equivocada produce probabilidades silenciosamente incorrectas
 *   (el softmax aún suma 1, pero la clase predicha es errónea).
 *
 * El resto de la arquitectura (LiteRT CompiledModel, aceleración GPU/CPU, softmax de salida)
 * es idéntica a [PlantDiseaseClassifier].
 */
class EfficientNetV2Classifier(
    private val context: Context,
) : LeafDiseaseClassifier {

    companion object {
        private const val TAG = "EfficientNetV2Classifier"
        private const val MODEL_PATH = "efficientnetv2b0_v1_fp16.tflite"
        private const val LABELS_PATH = "labels.txt"
        private const val INPUT_SIZE = 224
        private const val NUM_CHANNELS = 3
        private const val NUM_CLASSES = 38
        private const val CONFIDENCE_THRESHOLD = 0.5f
        private const val SOFTMAX_EPSILON = 0.01f

        // EfficientNetV2 con include_preprocessing=False: entrada normalizada a [0, 1]
        private const val PIXEL_SCALE = 255.0f
    }

    private val labels: List<String> = loadLabels()
    private var environment: Environment? = null
    private var compiledModel: CompiledModel? = null
    private var inputBuffers: MutableList<TensorBuffer> = mutableListOf()
    private var outputBuffers: MutableList<TensorBuffer> = mutableListOf()

    init {
        try {
            environment = Environment.create()
            environment?.let {
                val options = createModelOptions(it)
                compiledModel = CompiledModel.create(
                    context.assets,
                    MODEL_PATH,
                    options,
                    it
                )
            }
            inputBuffers  = compiledModel?.createInputBuffers()?.toMutableList()  ?: mutableListOf()
            outputBuffers = compiledModel?.createOutputBuffers()?.toMutableList() ?: mutableListOf()

            Log.d(TAG, "EfficientNetV2B0 initialized — accelerators=${environment?.getAvailableAccelerators()}")
        } catch (e: Exception) {
            close()
            Log.e(TAG, "Failed to initialize EfficientNetV2Classifier", e)
            throw e
        }
    }

    override fun classify(bitmap: Bitmap): ClassificationResult? {
        val model = compiledModel ?: run {
            Log.e(TAG, "CompiledModel not initialized")
            return null
        }

        return try {
            val startTime = SystemClock.uptimeMillis()
            val input = preprocessBitmap(bitmap)

            inputBuffers.firstOrNull()?.writeFloat(input)
                ?: error("Input tensor buffer not available")

            model.run(inputBuffers, outputBuffers)

            val rawOutput = outputBuffers.firstOrNull()?.readFloat()
                ?: error("Output tensor buffer not available")

            val probabilities = rawOutput.toProbabilities()
            val maxIndex = probabilities.indices.maxByOrNull(probabilities::get) ?: return null
            val confidence = probabilities[maxIndex]

            Log.i(TAG, "Inference in ${SystemClock.uptimeMillis() - startTime}ms — class=$maxIndex conf=${confidence * 100}")

            buildClassificationResult(maxIndex, confidence)
        } catch (e: Exception) {
            Log.e(TAG, "Error during EfficientNetV2 classification", e)
            null
        }
    }

    /**
     * Normaliza a [0, 1] dividiendo cada canal por 255.
     * EfficientNetV2 entrenado con include_preprocessing=False espera esta distribución.
     */
    private fun preprocessBitmap(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        val input  = FloatArray(INPUT_SIZE * INPUT_SIZE * NUM_CHANNELS)

        resizedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        var offset = 0
        for (pixel in pixels) {
            input[offset++] = ((pixel shr 16) and 0xFF) / PIXEL_SCALE  // R → [0, 1]
            input[offset++] = ((pixel shr  8) and 0xFF) / PIXEL_SCALE  // G → [0, 1]
            input[offset++] = ( pixel         and 0xFF) / PIXEL_SCALE  // B → [0, 1]
        }

        if (resizedBitmap !== bitmap && !resizedBitmap.isRecycled) {
            resizedBitmap.recycle()
        }

        return input
    }

    private fun createModelOptions(environment: Environment): CompiledModel.Options {
        val available = environment.getAvailableAccelerators()
        val preferred = buildList {
            if (available.contains(Accelerator.GPU)) add(Accelerator.GPU)
            add(Accelerator.CPU)
        }.toTypedArray()

        Log.d(TAG, "Accelerators available=$available; selected=${preferred.contentToString()}")
        return CompiledModel.Options(*preferred)
    }

    private fun loadLabels(): List<String> {
        return try {
            context.assets.open(LABELS_PATH).bufferedReader().useLines { lines ->
                lines.map(String::trim).filter(String::isNotEmpty).toList()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Labels file not found, falling back to PlantDiseaseInfo", e)
            PlantDiseaseInfo.getAllDiseaseNames()
        }
    }

    private fun buildClassificationResult(classIndex: Int, confidence: Float): ClassificationResult {
        val diseaseInfo = PlantDiseaseInfo.getDiseaseInfo(classIndex)
        if (diseaseInfo != null) {
            return ClassificationResult(
                diseaseName             = diseaseInfo.diseaseName,
                scientificName          = diseaseInfo.scientificName,
                confidence              = confidence,
                isHealthy               = diseaseInfo.isHealthy,
                description             = diseaseInfo.description,
                treatmentRecommendations = diseaseInfo.treatmentSteps,
                plantType               = diseaseInfo.plantType,
                confidenceLevel         = PlantDiseaseInfo.getConfidenceLevel(confidence)
            )
        }

        val fallbackLabel = labels.getOrNull(classIndex)
            ?.replace("___", " - ")
            ?.replace('_', ' ')
            ?: "Unknown disease"

        return ClassificationResult(
            diseaseName             = fallbackLabel,
            scientificName          = null,
            confidence              = confidence,
            isHealthy               = fallbackLabel.contains("healthy", ignoreCase = true),
            description             = "Predicción generada por EfficientNetV2B0.",
            treatmentRecommendations = emptyList(),
            plantType               = "Unknown",
            confidenceLevel         = PlantDiseaseInfo.getConfidenceLevel(confidence)
        )
    }

    private fun FloatArray.toProbabilities(): FloatArray {
        val sum = sum()
        val looksNormalized = sum > 0f && abs(sum - 1f) <= SOFTMAX_EPSILON && all { it in 0f..1f }
        return if (looksNormalized) this else applySoftmax(this)
    }

    private fun applySoftmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val expScores = logits.map { exp(it - maxLogit) }
        val sumExp = expScores.sum().takeIf { it > 0f } ?: return FloatArray(logits.size)
        return expScores.map { it / sumExp }.toFloatArray()
    }

    override fun close() {
        inputBuffers.forEach(TensorBuffer::close)
        outputBuffers.forEach(TensorBuffer::close)
        inputBuffers.clear()
        outputBuffers.clear()
        compiledModel?.close()
        compiledModel = null
        environment?.close()
        environment = null
    }
}
