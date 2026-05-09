package com.leafdisease.domain.model

/**
 * Modelos TFLite disponibles para experimentación comparativa.
 *
 * Cada entrada referencia el archivo .tflite en app/src/main/assets/
 * y su normalización de entrada requerida para inferencia correcta.
 *
 * IMPORTANTE — diferencias de preprocessing:
 *   MOBILENET_V2     → (pixel - 127.5f) / 127.5f  → rango [-1, 1]
 *   EFFICIENTNET_V2B0 → pixel / 255.0f             → rango [0, 1]
 *
 * Mezclar preprocessing y modelo produce predicciones silenciosamente incorrectas.
 */
enum class ModelType(
    val displayName: String,
    val modelPath: String,
    val labelsPath: String = "labels.txt",
    val inputRange: String,
) {
    MOBILENET_V2(
        displayName = "MobileNetV2 FP16",
        modelPath   = "mobilenetv2_v3_fp16.tflite",
        inputRange  = "[-1, 1]",
    ),
    EFFICIENTNET_V2B0(
        displayName = "EfficientNetV2B0 FP16",
        modelPath   = "efficientnetv2b0_v1_fp16.tflite",
        inputRange  = "[0, 1]",
    ),
}
