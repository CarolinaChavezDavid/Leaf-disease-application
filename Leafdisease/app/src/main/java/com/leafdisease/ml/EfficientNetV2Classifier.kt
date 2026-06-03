package com.leafdisease.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.domain.model.PlantDiseaseInfo
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.abs
import kotlin.math.exp

/**
 * Clasificador basado en EfficientNetV2B0 FP16.
 *
 * Usa el Interpreter clásico de LiteRT en lugar de CompiledModel porque
 * EfficientNetV2B0 contiene bloques Fused-MBConv que el compilador AOT de
 * CompiledModel no soporta. El Interpreter acepta todos los ops TFLite estándar.
 *
 * PREPROCESSING — diferencia crítica respecto a MobileNetV3 Small:
 *   EfficientNetV2 fue entrenado con `include_preprocessing=False` y normalización
 *   `pixel / 255.0` en el pipeline Python. La entrada esperada es [0, 1].
 *
 *   MobileNetV3 espera [-1, 1] → formula: `(pixel - 127.5f) / 127.5f`
 *   EfficientNetV2 espera [0, 1] → formula: `pixel / 255.0f`
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
        private const val SOFTMAX_EPSILON = 0.01f
        private const val NUM_THREADS = 4

        // EfficientNetV2 con include_preprocessing=False: entrada normalizada a [0, 1]
        private const val PIXEL_SCALE = 255.0f
    }

    private val labels: List<String> = loadLabels()
    private var interpreter: Interpreter? = null

    init {
        try {
            val modelBuffer = loadModelFromAssets()
            val options = Interpreter.Options().apply {
                numThreads = NUM_THREADS
            }
            interpreter = Interpreter(modelBuffer, options)
            Log.d(TAG, "EfficientNetV2B0 initialized with Interpreter (${NUM_THREADS} threads)")
        } catch (e: Exception) {
            close()
            Log.e(TAG, "Failed to initialize EfficientNetV2Classifier", e)
            throw e
        }
    }

    override fun classify(bitmap: Bitmap): ClassificationResult? {
        val interp = interpreter ?: run {
            Log.e(TAG, "Interpreter not initialized")
            return null
        }

        return try {
            val startTime = SystemClock.uptimeMillis()
            val input = preprocessBitmap(bitmap)

            // Input: [1, 224, 224, 3] — batch de 1 imagen
            val inputArray = Array(1) { Array(INPUT_SIZE) { Array(INPUT_SIZE) { FloatArray(NUM_CHANNELS) } } }
            var offset = 0
            for (y in 0 until INPUT_SIZE) {
                for (x in 0 until INPUT_SIZE) {
                    inputArray[0][y][x][0] = input[offset++]
                    inputArray[0][y][x][1] = input[offset++]
                    inputArray[0][y][x][2] = input[offset++]
                }
            }

            // Output: [1, 38]
            val outputArray = Array(1) { FloatArray(NUM_CLASSES) }
            interp.run(inputArray, outputArray)

            val rawOutput = outputArray[0]
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

        // === DIAGNÓSTICO — eliminar después de verificar ===
        val rawFirst = pixels.take(2).map { p ->
            Triple((p shr 16) and 0xFF, (p shr 8) and 0xFF, p and 0xFF)
        }
        Log.d(TAG, "DIAG raw pixels[0] R=${rawFirst[0].first} G=${rawFirst[0].second} B=${rawFirst[0].third}")
        Log.d(TAG, "DIAG raw pixels[1] R=${rawFirst[1].first} G=${rawFirst[1].second} B=${rawFirst[1].third}")
        Log.d(TAG, "DIAG input[0..5]=${input.take(6).map { "%.4f".format(it) }}")
        Log.d(TAG, "DIAG input stats min=${"%.4f".format(input.min())} max=${"%.4f".format(input.max())} mean=${"%.4f".format(input.average())}")
        Log.d(TAG, "DIAG bitmap size before resize=${bitmap.width}x${bitmap.height} after=224x224")
        // === FIN DIAGNÓSTICO ===

        if (resizedBitmap !== bitmap && !resizedBitmap.isRecycled) {
            resizedBitmap.recycle()
        }

        return input
    }

    private fun loadModelFromAssets(): MappedByteBuffer {
        return context.assets.openFd(MODEL_PATH).use { fd ->
            FileInputStream(fd.fileDescriptor).channel.map(
                FileChannel.MapMode.READ_ONLY,
                fd.startOffset,
                fd.declaredLength,
            )
        }
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
        val rawLabel = labels.getOrNull(classIndex) ?: ""

        PlantDiseaseInfo.getClassificationResult(classIndex, confidence, rawLabel, context)
            ?.let { return it }

        val fallbackLabel = rawLabel
            .replace("___", " - ")
            .replace('_', ' ')
            .ifEmpty { "Unknown disease" }

        return ClassificationResult(
            diseaseName              = fallbackLabel,
            scientificName           = null,
            confidence               = confidence,
            isHealthy                = fallbackLabel.contains("healthy", ignoreCase = true),
            description              = "Predicción generada por EfficientNetV2B0.",
            treatmentRecommendations = emptyList(),
            plantType                = "Unknown",
            confidenceLevel          = PlantDiseaseInfo.getConfidenceLevel(confidence),
            rawLabel                 = rawLabel,
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
        interpreter?.close()
        interpreter = null
    }
}
