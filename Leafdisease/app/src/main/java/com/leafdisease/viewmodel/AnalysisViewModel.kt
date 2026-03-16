package com.leafdisease.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.ml.LeafDiseaseClassifier
import com.leafdisease.ml.PlantDiseaseClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * States for the analysis process.
 */
sealed class AnalysisState {
    object Idle : AnalysisState()
    object LoadingModel : AnalysisState()
    data class Preprocessing(val progress: Int) : AnalysisState()
    data class Analyzing(val progress: Int) : AnalysisState()
    data class Success(val result: ClassificationResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

/**
 * ViewModel that manages the leaf disease analysis workflow.
 * Handles model loading, image preprocessing, and inference.
 */
class AnalysisViewModel(
    private val context: Context
) : ViewModel() {

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private var classifier: LeafDiseaseClassifier? = null
    private var currentImagePath: String? = null
    private var currentImageRotation: Int = 0

    companion object {
        private const val PREPROCESS_STEPS = 30
        private const val ANALYSIS_STEPS = 70
        private const val STEP_DELAY_MS = 30L
    }

    /**
     * Initialize the classifier. Should be called when the screen is created.
     */
    fun initializeClassifier() {
        viewModelScope.launch {
            try {
                _analysisState.value = AnalysisState.LoadingModel

                // Initialize classifier on background thread
                withContext(Dispatchers.IO) {
                    classifier = PlantDiseaseClassifier(context = context)
                }

                // Simulate model loading steps
                for (i in 0..PREPROCESS_STEPS) {
                    _analysisState.value = AnalysisState.Preprocessing(i)
                    delay(STEP_DELAY_MS)
                }

            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(
                    "Failed to load model: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Analyze an image file.
     * @param imagePath Path to the captured/selected image
     * @param rotationDegrees Rotation of the image in degrees
     */
    fun analyzeImage(imagePath: String, rotationDegrees: Int = 0) {
        currentImagePath = imagePath
        currentImageRotation = rotationDegrees

        viewModelScope.launch {
            try {
                // Load and preprocess image
                val bitmap = withContext(Dispatchers.IO) {
                    loadAndPreprocessImage(imagePath)
                }

                if (bitmap == null) {
                    _analysisState.value = AnalysisState.Error("Failed to load image")
                    return@launch
                }

                // Simulate analysis steps with progress
                for (i in 0..ANALYSIS_STEPS) {
                    _analysisState.value = AnalysisState.Analyzing(PREPROCESS_STEPS + i)
                    delay(STEP_DELAY_MS)
                }

                // Perform actual inference with rotation
                val result = withContext(Dispatchers.Default) {
                    classifier?.classify(bitmap, rotationDegrees)
                }

                if (result != null) {
                    _analysisState.value = AnalysisState.Success(result)
                } else {
                    _analysisState.value = AnalysisState.Error(
                        "Could not classify the image. Please try again with a clearer photo."
                    )
                }

                // Clean up bitmap
                bitmap.recycle()

            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(
                    "Analysis failed: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Load and preprocess the image for inference.
     */
    private fun loadAndPreprocessImage(imagePath: String): Bitmap? {
        return try {
            val file = File(imagePath)
            if (!file.exists()) return null

            // Decode image bounds
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imagePath, options)

            // Calculate sample size to reduce memory usage
            val maxDimension = 1024
            val sampleSize = calculateInSampleSize(options, maxDimension, maxDimension)

            // Decode with sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            BitmapFactory.decodeFile(imagePath, decodeOptions)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculate sample size for bitmap downsampling.
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Reset the analysis state to idle.
     */
    fun reset() {
        _analysisState.value = AnalysisState.Idle
        currentImagePath = null
    }

    /**
     * Retry analysis with the current image.
     */
    fun retry() {
        currentImagePath?.let { path ->
            analyzeImage(path)
        } ?: run {
            _analysisState.value = AnalysisState.Error("No image to analyze")
        }
    }

    override fun onCleared() {
        super.onCleared()
        classifier?.close()
    }

    /**
     * Factory for creating AnalysisViewModel with context.
     */
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
                return AnalysisViewModel(context.applicationContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
