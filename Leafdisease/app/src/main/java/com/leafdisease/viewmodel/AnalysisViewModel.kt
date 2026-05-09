package com.leafdisease.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leafdisease.domain.model.BenchmarkMetrics
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.domain.model.ModelType
import com.leafdisease.ml.ClassifierFactory
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
 * Estados para el flujo de análisis.
 */
sealed class AnalysisState {
    object Idle : AnalysisState()
    object LoadingModel : AnalysisState()
    object Preprocessing : AnalysisState()
    object Analyzing : AnalysisState()
    data class Success(
        val result: ClassificationResult,
        /** Métricas acumuladas hasta esta inferencia. Null si el clasificador no es BenchmarkClassifier. */
        val metrics: BenchmarkMetrics? = null,
    ) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

/**
 * ViewModel que gestiona el flujo de análisis de enfermedades en plantas.
 *
 * Cambios respecto al baseline:
 *   - Recibe [ClassifierFactory] en lugar de [com.leafdisease.ml.LeafDiseaseClassifier]
 *   - Expone [selectedModel] para que HomeScreen lo observe y cambie via [setModelType]
 *   - [AnalysisState.Success] incluye [BenchmarkMetrics] con métricas de inferencia acumuladas
 */
class AnalysisViewModel(
    private val classifierFactory: ClassifierFactory,
    initialModelType: ModelType = ModelType.MOBILENET_V2,
) : ViewModel() {

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _selectedModel = MutableStateFlow(initialModelType)
    val selectedModel: StateFlow<ModelType> = _selectedModel.asStateFlow()

    private var currentImagePath: String? = null
    private var isClassifierInitialized = false

    /**
     * Cambia el modelo activo. Llámalo antes de [startAnalysis].
     * El nuevo modelo se cargará (con medición de init time) en la primera inferencia.
     */
    fun setModelType(modelType: ModelType) {
        _selectedModel.value = modelType
        Log.d(TAG, "Model type set to: ${modelType.displayName}")
    }

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
                Log.d(TAG, "Preprocessing completed in ${preprocessDuration}ms")

                if (bitmap == null) {
                    _analysisState.value = AnalysisState.Error("Failed to load image")
                    return@launch
                }

                _analysisState.value = AnalysisState.Analyzing

                val activeModelType = _selectedModel.value

                val (result, metrics) = withContext(Dispatchers.Default) {
                    val classifier = classifierFactory.getOrCreate(activeModelType)
                    val r = classifier.classify(bitmap)
                    val m = classifier.getMetrics()
                    Pair(r, m)
                }

                bitmap.recycle()

                if (result != null) {
                    _analysisState.value = AnalysisState.Success(result, metrics)
                    Log.i(TAG, "Analysis successful [${activeModelType.displayName}] — " +
                            "avg=${metrics.avgMs}ms P95=${metrics.p95Ms}ms samples=${metrics.sampleCount}")
                } else {
                    _analysisState.value = AnalysisState.Error(
                        "No se pudo clasificar la imagen. Intenta con una foto más clara."
                    )
                }

            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(
                    "Analysis failed: ${e.localizedMessage}"
                )
            }
        }
    }

    fun analyzeImage(imagePath: String) = startAnalysis(imagePath)

    private fun loadAndPreprocessImage(imagePath: String): Bitmap? {
        return try {
            val file = File(imagePath)
            if (!file.exists()) return null

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(imagePath, options)

            val maxDimension = 1024
            val sampleSize = calculateInSampleSize(options, maxDimension, maxDimension)

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize      = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            BitmapFactory.decodeFile(imagePath, decodeOptions)
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width  = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth  = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun reset() {
        _analysisState.value = AnalysisState.Idle
        currentImagePath = null
    }

    fun retry() {
        currentImagePath?.let { startAnalysis(it) }
            ?: run { _analysisState.value = AnalysisState.Error("No image to analyze") }
    }

    override fun onCleared() {
        super.onCleared()
    }

    class Factory @Inject constructor(
        private val classifierFactory: ClassifierFactory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
                return AnalysisViewModel(classifierFactory) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
