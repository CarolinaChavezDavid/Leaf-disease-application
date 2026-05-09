package com.leafdisease.ml

import kotlin.math.pow
import kotlin.math.sqrt

// Extensiones internas para cálculos estadísticos de benchmark.
// Solo se usan dentro de BenchmarkClassifier.

internal fun List<Double>.median(): Double {
    if (isEmpty()) return 0.0
    val sorted = sorted()
    return sorted[sorted.size / 2]
}

internal fun List<Double>.percentile(p: Int): Double {
    if (isEmpty()) return 0.0
    val sorted = sorted()
    val index = ((sorted.size - 1) * p / 100.0).toInt().coerceIn(0, sorted.size - 1)
    return sorted[index]
}

internal fun List<Double>.std(): Double {
    if (size < 2) return 0.0
    val mean = average()
    return sqrt(sumOf { (it - mean).pow(2) } / size)
}
