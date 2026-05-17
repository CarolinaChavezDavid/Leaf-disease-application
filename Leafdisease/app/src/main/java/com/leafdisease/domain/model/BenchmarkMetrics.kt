package com.leafdisease.domain.model

/**
 * Métricas de rendimiento on-device recolectadas por [com.leafdisease.ml.BenchmarkClassifier].
 *
 * Corresponde directamente al protocolo experimental definido en EXPERIMENTAL_PLAN.md:
 *   - 3 sesiones × 50 inferencias por modelo
 *   - La primera inferencia se reporta como warmup (no entra en avg/P95/std)
 *   - Las siguientes (steady-state) alimentan avg, mediana, P95 y std
 *
 * Unidades: tiempos en milisegundos, memoria en MB.
 */
data class BenchmarkMetrics(
    /** Modelo que generó estas métricas. */
    val modelType: ModelType,

    /** Tiempo de creación del intérprete LiteRT (carga del archivo .tflite). */
    val initTimeMs: Long,

    /** Primera inferencia (warmup). Siempre mayor que el steady-state por JIT. */
    val warmupMs: Double,

    /** Media de las inferencias steady-state (run 2..N). */
    val avgMs: Double,

    /** Mediana (P50) de las inferencias steady-state. */
    val medianMs: Double,

    /**
     * Percentil 95 de las inferencias steady-state.
     * Métrica más relevante para UX: indica el peor caso en el 95% de las ejecuciones.
     */
    val p95Ms: Double,

    /** Desviación estándar de las inferencias steady-state. */
    val stdMs: Double,

    /**
     * Memoria PSS (Proportional Set Size) total del proceso en el momento de lectura.
     * Capturada vía [android.os.Debug.getMemoryInfo].
     */
    val totalPssMb: Double,

    /** Número de inferencias steady-state que alimentan avg/P95/std (excluye warmup). */
    val sampleCount: Int,

    /** Etiqueta (diseaseName) de la clase con mayor probabilidad en la última inferencia. */
    val topLabel: String,

    /** Confianza (0..1) de la clase con mayor probabilidad en la última inferencia. */
    val topConfidence: Float,
)
