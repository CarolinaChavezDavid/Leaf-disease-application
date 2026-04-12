package com.leafdisease.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.ml.LeafDiseaseClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

const val TAG = "AnalysisViewModel"
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
    private val classifier: LeafDiseaseClassifier
) : ViewModel() {

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private var currentImagePath: String? = null
    private var isClassifierInitialized = false

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
                if (isClassifierInitialized) return@launch

                _analysisState.value = AnalysisState.LoadingModel

                // Simulate model loading steps
                for (i in 0..PREPROCESS_STEPS) {
                    _analysisState.value = AnalysisState.Preprocessing(i)
                    delay(STEP_DELAY_MS)
                }

                isClassifierInitialized = true

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
     */
    fun analyzeImage(imagePath: String) {
        currentImagePath = imagePath

        viewModelScope.launch {
            try {
                _analysisState.value = AnalysisState.Preprocessing(0)

                // Load and preprocess image
                val bitmap = withContext(Dispatchers.IO) {
                    loadAndPreprocessImage(imagePath)
                }

                if (bitmap == null) {
                    _analysisState.value = AnalysisState.Error("Failed to load image")
                    Log.e(TAG, "Failed to load image")
                    return@launch
                }

                // Simulate analysis steps with progress
                for (i in 0..ANALYSIS_STEPS) {
                    _analysisState.value = AnalysisState.Analyzing(PREPROCESS_STEPS + i)
                    delay(STEP_DELAY_MS)
                }

                // Perform actual inference with rotation
                val result = withContext(Dispatchers.Default) {
                    classifier.classify(bitmap)
                }

                if (result != null) {
                    _analysisState.value = AnalysisState.Success(result)
                    Log.i(TAG, "Analysis successful $result")
                } else {
                    Log.e(TAG, "Analysis Failed")

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
    }

    /**
     * Factory for creating AnalysisViewModel with context.
     */
    class Factory @Inject constructor(
        private val classifier: LeafDiseaseClassifier
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
                return AnalysisViewModel(classifier) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
