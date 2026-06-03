package com.leafdisease.ml

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.leafdisease.domain.model.ModelType

/**
 * Fábrica singleton que crea y cachea un [BenchmarkClassifier] por cada [ModelType].
 *
 * Gestión del ciclo de vida:
 *   - La primera llamada a [getOrCreate] para un tipo crea el clasificador y mide
 *     el tiempo de inicialización del intérprete LiteRT.
 *   - Las llamadas siguientes con el mismo tipo devuelven la instancia en caché
 *     (sin reinicializar el modelo).
 *   - [closeAll] libera todos los clasificadores en caché.
 *
 * Prerequisito: el archivo `.tflite` de cada modelo debe existir en `assets/` antes
 * de compilar la app. Ver ANDROID_PROJECT.md para instrucciones de copia.
 */
class ClassifierFactory(private val context: Context) {

    companion object {
        private const val TAG = "ClassifierFactory"
    }

    private val cache = mutableMapOf<ModelType, BenchmarkClassifier>()

    /**
     * Devuelve el [BenchmarkClassifier] para el [modelType] dado.
     * Crea el clasificador la primera vez; devuelve caché en llamadas posteriores.
     *
     * @throws Exception si el archivo .tflite no existe en assets o falla la carga.
     */
    fun getOrCreate(modelType: ModelType): BenchmarkClassifier {
        return cache.getOrPut(modelType) {
            Log.d(TAG, "Creating classifier for ${modelType.displayName}...")

            val initStart = SystemClock.elapsedRealtimeNanos()
            val delegate: LeafDiseaseClassifier = when (modelType) {
                ModelType.MOBILENET_V3_SMALL      -> PlantDiseaseClassifier(context)
                ModelType.EFFICIENTNET_V2B0 -> EfficientNetV2Classifier(context)
            }
            val initTimeMs = (SystemClock.elapsedRealtimeNanos() - initStart) / 1_000_000L

            Log.d(TAG, "${modelType.displayName} loaded in ${initTimeMs}ms")

            BenchmarkClassifier(
                delegate    = delegate,
                modelType   = modelType,
                initTimeMs  = initTimeMs,
            )
        }
    }

    /** Libera todos los clasificadores en caché y limpia el mapa. */
    fun closeAll() {
        cache.values.forEach { it.close() }
        cache.clear()
        Log.d(TAG, "All classifiers closed")
    }
}
