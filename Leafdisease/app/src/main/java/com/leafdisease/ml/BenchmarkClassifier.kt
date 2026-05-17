package com.leafdisease.ml

import android.graphics.Bitmap
import android.os.Debug
import android.os.SystemClock
import android.util.Log
import com.leafdisease.domain.model.BenchmarkMetrics
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.domain.model.ModelType

/**
 * Wrapper decorador sobre cualquier [LeafDiseaseClassifier] que instrumenta
 * métricas de rendimiento sin contaminar el código del clasificador base.
 *
 * Protocolo de medición (alineado con EXPERIMENTAL_PLAN.md):
 *   - Primera llamada a [classify] → warmup (excluida de avg/P95/std)
 *   - Llamadas 2..N → steady-state (alimentan las estadísticas)
 *   - [getMetrics] puede llamarse en cualquier momento para leer el estado actual
 *
 * Las métricas se acumulan a lo largo de la vida del objeto. Para empezar una nueva sesión
 * sin reiniciar la app, llama a [resetTimings].
 */
class BenchmarkClassifier(
    private val delegate: LeafDiseaseClassifier,
    val modelType: ModelType,
    /** Tiempo de inicialización del intérprete LiteRT medido externamente (ms). */
    val initTimeMs: Long,
) : LeafDiseaseClassifier {

    companion object {
        private const val TAG = "BenchmarkClassifier"
        private const val CONFIDENCE_THRESHOLD = 0.5f
    }

    private val timingsNs = mutableListOf<Long>()
    private var warmupNs  = 0L

    private var lastTopLabel: String = ""
    private var lastTopConfidence: Float = 0f

    override fun classify(bitmap: Bitmap): ClassificationResult? {
        val t0     = SystemClock.elapsedRealtimeNanos()
        val result = delegate.classify(bitmap)
        val elapsed = SystemClock.elapsedRealtimeNanos() - t0

        if (timingsNs.isEmpty() && warmupNs == 0L) {
            // Primera inferencia = warmup
            warmupNs = elapsed
            Log.d(TAG, "[${modelType.displayName}] warmup=${elapsed / 1_000_000}ms")
        } else {
            timingsNs.add(elapsed)
            Log.d(TAG, "[${modelType.displayName}] inference[${timingsNs.size}]=${elapsed / 1_000_000}ms")
        }

        if (result == null) return null  // error real del modelo

        lastTopLabel = result.diseaseName
        lastTopConfidence = result.confidence
        Log.d(TAG, "[${modelType.displayName}] top=${result.diseaseName} conf=${"%.2f".format(result.confidence)}")

        return if (result.confidence < CONFIDENCE_THRESHOLD) null else result
    }

    /**
     * Devuelve las métricas acumuladas hasta el momento.
     *
     * Memoria PSS capturada en el momento de la llamada. Para una lectura precisa
     * llama desde el hilo principal o desde un contexto con el proceso estable.
     */
    fun getMetrics(): BenchmarkMetrics {
        val ms = timingsNs.map { it / 1_000_000.0 }

        val memInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memInfo)
        val totalPssMb = memInfo.totalPss / 1024.0

        return BenchmarkMetrics(
            modelType      = modelType,
            initTimeMs     = initTimeMs,
            warmupMs       = if (warmupNs > 0L) warmupNs / 1_000_000.0 else 0.0,
            avgMs          = if (ms.isNotEmpty()) ms.average() else 0.0,
            medianMs       = ms.median(),
            p95Ms          = ms.percentile(95),
            stdMs          = ms.std(),
            totalPssMb     = totalPssMb,
            sampleCount    = ms.size,
            topLabel       = lastTopLabel,
            topConfidence  = lastTopConfidence,
        )
    }

    /**
     * Reinicia los tiempos de inferencia sin cerrar el modelo.
     * Usar entre sesiones experimentales cuando no se desea reiniciar la app.
     */
    fun resetTimings() {
        timingsNs.clear()
        warmupNs = 0L
        lastTopLabel = ""
        lastTopConfidence = 0f
        Log.d(TAG, "[${modelType.displayName}] timings reset")
    }

    override fun close() = delegate.close()
}
