package com.leafdisease.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.ai.edge.litert.TensorBuffer
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.domain.model.PlantDiseaseInfo
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Interface for leaf disease classification.
 */
interface LeafDiseaseClassifier {
    /**
     * Classify a leaf disease from a bitmap image.
     * @param bitmap The input image bitmap
     * @param rotationDegrees Rotation of the image (0, 90, 180, 270)
     * @return ClassificationResult with disease information
     */
    fun classify(bitmap: Bitmap, rotationDegrees: Int = 0): ClassificationResult?

    /**
     * Close the classifier and release resources.
     */
    fun close()
}

/**
 * TensorFlow Lite implementation of the leaf disease classifier.
 * Uses MobileNetV2 model trained on PlantVillage dataset.
 */
class PlantDiseaseClassifier(
    private val context: Context,
    private val useGpu: Boolean = false
) : LeafDiseaseClassifier {

    companion object {
        private const val TAG = "TfliteClassifier"
        private const val INPUT_SIZE = 224 // MobileNetV2 input size
        private const val NUM_CLASSES = 38 // PlantVillage classes
        private const val MEAN = 0f // Normalization mean
        private const val STD = 255f // Normalization std (0-1 range)
        private const val CONFIDENCE_THRESHOLD = 0.5f
        private const val NUM_THREADS = 4
        private const val LABELS_PATH: String = "labels.txt"
    }

    val model = Mobilenetv2V3Fp16.newInstance(context)

    private val labels: List<String> by lazy {
        runCatching { FileUtil.loadLabels(context, "labels.txt") }
            .getOrElse { emptyList() }
    }

    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
    inputFeature0.loadBuffer(byteBuffer)

    // Runs model inference and gets result.
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    // Releases model resources if no longer used.
    model.close()


    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private val labels: List<String>

    init {
        try {
            // Load model
            val model = loadModel()
            interpreter = createInterpreter(model)

            // Load labels
            labels = loadLabels()





            Log.d(TAG, "Classifier initialized successfully with ${labels.size} classes")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize classifier", e)
            throw e
        }
    }


    /**
     * Create interpreter with optimized settings.
     */
    private fun createInterpreter(model: ByteBuffer): Interpreter {
        val options = Interpreter.Options().apply {
            setNumThreads(NUM_THREADS)
            useNNAPI = !useGpu // Use NNAPI if not using GPU
            addDelegate(GpuDelegate())
        }

        return Interpreter(model, options)
    }

    /**
     * Load disease labels from assets.
     */
    private fun loadLabels(): List<String> {
        return try {
            FileUtil.loadLabels(context, LABELS_PATH)
        } catch (e: IOException) {
            Log.w(TAG, " $e Labels file not found, using PlantVillage default labels")
            PlantDiseaseInfo.getAllDiseaseNames()
        }
    }

    override fun classify(bitmap: Bitmap, rotationDegrees: Int): ClassificationResult? {
        if (interpreter == null) {
            Log.e(TAG, "Interpreter not initialized")
            return null
        }

        return try {
            val startTime = SystemClock.uptimeMillis()

            // Create image processor with the correct rotation for this image
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(Rot90Op(-rotationDegrees / 90))
                .add(NormalizeOp(MEAN, STD))
                .build()

            // Create fresh input buffer and load image
            val inputImageBuffer = TensorImage.fromBitmap(bitmap)
            val processedImage = imageProcessor.process(inputImageBuffer)

            // Output buffer for float array [batch_size, num_classes]
            val outputBuffer = ByteBuffer.allocateDirect(4 * NUM_CLASSES).apply {
                order(ByteOrder.nativeOrder())
            }

            // Run inference
            interpreter?.run(processedImage.buffer, outputBuffer)

            // Get results
            val results = parseResults(outputBuffer)

            val inferenceTime = SystemClock.uptimeMillis() - startTime
            Log.d(TAG, "Inference completed in ${inferenceTime}ms")

            results.firstOrNull()?.takeIf { it.confidence >= CONFIDENCE_THRESHOLD }
        } catch (e: Exception) {
            Log.e(TAG, "Error during classification", e)
            null
        }
    }

    /**
     * Parse the raw output buffer into classification results.
     */
    private fun parseResults(outputBuffer: ByteBuffer): List<ClassificationResult> {
        outputBuffer.rewind()
        val probabilities = FloatArray(NUM_CLASSES)

        // Read probabilities from buffer
        for (i in 0 until NUM_CLASSES) {
            probabilities[i] = outputBuffer.float
        }

        // Apply softmax if model doesn't include it
        val softmaxProbabilities = applySoftmax(probabilities)

        // Get top prediction
        val maxIndex = probabilities.indices.maxByOrNull { softmaxProbabilities[it] } ?: 0
        val maxConfidence = softmaxProbabilities[maxIndex]

        val diseaseInfo = PlantDiseaseInfo.getDiseaseInfo(maxIndex)

        return if (diseaseInfo != null) {
            listOf(
                ClassificationResult(
                    diseaseName = diseaseInfo.diseaseName,
                    scientificName = diseaseInfo.scientificName,
                    confidence = maxConfidence,
                    isHealthy = diseaseInfo.isHealthy,
                    description = diseaseInfo.description,
                    treatmentRecommendations = diseaseInfo.treatmentSteps,
                    confidenceLevel = PlantDiseaseInfo.getConfidenceLevel(maxConfidence)
                )
            )
        } else {
            emptyList()
        }
    }

    /**
     * Apply softmax to raw logits.
     */
    private fun applySoftmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val expScores = logits.map { kotlin.math.exp(it - maxLogit) }
        val sumExp = expScores.sum()
        return expScores.map { it / sumExp }.toFloatArray()
    }

    override fun close() {
        try {
            interpreter?.close()
            interpreter = null
            gpuDelegate?.close()
            gpuDelegate = null
            Log.d(TAG, "Classifier closed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing classifier", e)
        }
    }
}
