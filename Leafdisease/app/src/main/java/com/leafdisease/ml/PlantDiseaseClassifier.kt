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
 * Interface for leaf disease classification.
 */
interface LeafDiseaseClassifier {
    /**
     * Classify a leaf disease from a bitmap image.
     * @param bitmap The input image bitmap
     * @return ClassificationResult with disease information
     */
    fun classify(bitmap: Bitmap): ClassificationResult?

    /**
     * Close the classifier and release resources.
     */
    fun close()
}

/**
 * LiteRT implementation of the leaf disease classifier.
 * Uses a compiled MobileNetV2 FP16 model from the app assets.
 */
class PlantDiseaseClassifier(
    private val context: Context,
) : LeafDiseaseClassifier {

    companion object {
        private const val TAG = "PlantDiseaseClassifier"
        private const val MODEL_PATH = "mobilenetv2_v3_fp16.tflite"
        private const val LABELS_PATH = "labels.txt"
        private const val INPUT_SIZE = 224
        private const val NUM_CHANNELS = 3
        private const val NUM_CLASSES = 38
        private const val PIXEL_MEAN = 127.5f
        private const val PIXEL_STD = 127.5f
        private const val CONFIDENCE_THRESHOLD = 0.5f
        private const val SOFTMAX_EPSILON = 0.01f
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

            inputBuffers = compiledModel?.createInputBuffers()?.toMutableList() ?: mutableListOf()
            outputBuffers = compiledModel?.createOutputBuffers()?.toMutableList() ?: mutableListOf()

            Log.d(
                TAG,
                "Classifier initialized with accelerators=${environment?.getAvailableAccelerators()}"
            )
        } catch (e: Exception) {
            close()
            Log.e(TAG, " Failed to initialize classifier", e)
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

            Log.i(TAG, "Raw output: ${rawOutput.contentToString()}")
            Log.i(TAG, "Probabilities: ${probabilities.contentToString()}")
            Log.i(TAG, "maxIndex $maxIndex")

            val inferenceTime = SystemClock.uptimeMillis() - startTime
            Log.d(TAG, "Inference completed in ${inferenceTime}ms")

            if (confidence < CONFIDENCE_THRESHOLD) {
                return null
            }

            buildClassificationResult(maxIndex, confidence)
        } catch (e: Exception) {
            Log.e(TAG, "Error during classification", e)
            null
        }
    }

    private fun preprocessBitmap(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        val input = FloatArray(INPUT_SIZE * INPUT_SIZE * NUM_CHANNELS)

        resizedBitmap.getPixels(
            pixels,
            0,
            INPUT_SIZE,
            0,
            0,
            INPUT_SIZE,
            INPUT_SIZE
        )

        var offset = 0
        for (pixel in pixels) {
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF

            input[offset++] = (red - PIXEL_MEAN) / PIXEL_STD
            input[offset++] = (green - PIXEL_MEAN) / PIXEL_STD
            input[offset++] = (blue - PIXEL_MEAN) / PIXEL_STD
        }

        if (resizedBitmap !== bitmap && !resizedBitmap.isRecycled) {
            resizedBitmap.recycle()
        }

        return input
    }

    private fun createModelOptions(environment: Environment): CompiledModel.Options {
        val availableAccelerators = environment.getAvailableAccelerators()
        val preferredAccelerators = buildList {
            if (availableAccelerators.contains(Accelerator.GPU)) {
                add(Accelerator.GPU)
            }
            add(Accelerator.CPU)
        }.toTypedArray()

        return CompiledModel.Options(*preferredAccelerators)
    }

    private fun loadLabels(): List<String> {
        return try {
            context.assets.open(LABELS_PATH).bufferedReader().useLines { lines ->
                lines.map(String::trim)
                    .filter(String::isNotEmpty)
                    .toList()
            }
        } catch (e: IOException) {
            Log.w(TAG, "Labels file not found, using PlantDiseaseInfo defaults", e)
            PlantDiseaseInfo.getAllDiseaseNames()
        }
    }

    private fun buildClassificationResult(
        classIndex: Int,
        confidence: Float,
    ): ClassificationResult {
        val diseaseInfo = PlantDiseaseInfo.getDiseaseInfo(classIndex)
        if (diseaseInfo != null) {
            return ClassificationResult(
                diseaseName = diseaseInfo.diseaseName,
                scientificName = diseaseInfo.scientificName,
                confidence = confidence,
                isHealthy = diseaseInfo.isHealthy,
                description = diseaseInfo.description,
                treatmentRecommendations = diseaseInfo.treatmentSteps,
                confidenceLevel = PlantDiseaseInfo.getConfidenceLevel(confidence)
            )
        }

        val fallbackLabel = labels.getOrNull(classIndex)
            ?.replace("___", " - ")
            ?.replace('_', ' ')
            ?: "Unknown disease"

        return ClassificationResult(
            diseaseName = fallbackLabel,
            scientificName = null,
            confidence = confidence,
            isHealthy = fallbackLabel.contains("healthy", ignoreCase = true),
            description = "Prediction generated from the LiteRT model.",
            treatmentRecommendations = emptyList(),
            confidenceLevel = PlantDiseaseInfo.getConfidenceLevel(confidence)
        )
    }

    private fun FloatArray.toProbabilities(): FloatArray {
        if (size != NUM_CLASSES) {
            Log.w(TAG, "Unexpected output size: $size")
        }

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
