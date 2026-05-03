package com.leafdisease.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.ml.LeafDiseaseClassifier
import kotlinx.coroutines.Dispatchers
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
    object Preprocessing : AnalysisState()
    object Analyzing : AnalysisState()
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

    /**
     * Initialize the classifier. Should be called when the screen is created.
     */
    fun initializeClassifier() {
        viewModelScope.launch {
            try {
                if (isClassifierInitialized) return@launch

                _analysisState.value = AnalysisState.LoadingModel
                isClassifierInitialized = true
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(
                    "Failed to load model: ${e.localizedMessage}"
                )
            }
        }
    }

    fun startAnalysis(imagePath: String) {
        currentImagePath = imagePath

        viewModelScope.launch {
            try {
                if (!isClassifierInitialized) {
                    _analysisState.value = AnalysisState.LoadingModel
                    isClassifierInitialized = true
                }

                _analysisState.value = AnalysisState.Preprocessing
                val preprocessStart = SystemClock.elapsedRealtime()

                val bitmap = withContext(Dispatchers.IO) {
                    loadAndPreprocessImage(imagePath)
                }

                val preprocessDuration = SystemClock.elapsedRealtime() - preprocessStart
                Log.d(TAG, "Image preprocessing completed in ${preprocessDuration}ms")

                if (bitmap == null) {
                    _analysisState.value = AnalysisState.Error("Failed to load image")
                    Log.e(TAG, "Failed to load image")
                    return@launch
                }

                _analysisState.value = AnalysisState.Analyzing
                val inferenceStart = SystemClock.elapsedRealtime()

                val result = withContext(Dispatchers.Default) {
                    classifier.classify(bitmap)
                }

                val inferenceDuration = SystemClock.elapsedRealtime() - inferenceStart
                Log.d(TAG, "Image inference completed in ${inferenceDuration}ms")

                if (result != null) {
                    _analysisState.value = AnalysisState.Success(result)
                    Log.i(TAG, "Analysis successful $result")
                } else {
                    Log.e(TAG, "Analysis Failed")

                    _analysisState.value = AnalysisState.Error(
                        "Could not classify the image. Please try again with a clearer photo."
                    )
                }

                bitmap.recycle()

            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(
                    "Analysis failed: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Analyze an image file.
     * @param imagePath Path to the captured/selected image
     */
    fun analyzeImage(imagePath: String) {
        startAnalysis(imagePath)
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
