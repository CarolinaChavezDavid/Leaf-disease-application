# Leaf Scan: detección de enfermedades foliares en Android

## Descripción del proyecto

**Nombre:** Leaf Scan (prototipo de investigación, sin relación con aplicaciones comerciales de nombre similar)

Aplicación Android nativa que clasifica enfermedades en hojas de plantas usando modelos de aprendizaje profundo corriendo directamente en el dispositivo (on-device inference), sin dependencia de servidores externos.

**Stack tecnológico:**
| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin 100% |
| UI | Jetpack Compose (sin layouts XML) |
| Motor de inferencia | Google AI Edge LiteRT: `CompiledModel` para MobileNetV3 Small (GPU si está disponible, si no CPU) e `Interpreter` con 4 hilos de CPU para EfficientNetV2B0 |
| Cámara | CameraX |
| Inyección de dependencias | Dagger 2 + KSP |
| Arquitectura | MVVM (Model-View-ViewModel) |
| Async | Kotlin Coroutines + StateFlow |

- **Versión mínima Android:** API 24 (Android 7.0)
- **Target SDK:** 36

**Modelos incluidos** (`app/src/main/assets/`), entrenados sobre PlantVillage (38 clases) y cuantizados en FP16:

| Modelo | Archivo | Tamaño | Motor |
|---|---|---|---|
| MobileNetV3 Small | `mobilenetv3_small_v1_fp16.tflite` | 1,91 MB | `CompiledModel` |
| EfficientNetV2B0 | `efficientnetv2b0_v1_fp16.tflite` | 11,33 MB | `Interpreter` |

EfficientNetV2B0 usa el `Interpreter` porque sus bloques Fused-MBConv no son compatibles con el compilador AOT de `CompiledModel`.

---

## Arquitectura actual

### Diagrama de capas

```
┌─────────────────────────────────────────┐
│  Presentation (Jetpack Compose)          │
│  HomeScreen → AnalyzingScreen →          │
│  ResultScreen, TakePhotoScreen           │
├─────────────────────────────────────────┤
│  ViewModel                               │
│  AnalysisViewModel (StateFlow)           │
├─────────────────────────────────────────┤
│  Domain                                  │
│  ClassificationResult, ModelType,        │
│  BenchmarkMetrics, PlantDiseaseInfo      │
├─────────────────────────────────────────┤
│  ML / Data                               │
│  ClassifierFactory → BenchmarkClassifier │
│  PlantDiseaseClassifier (MobileNetV3)    │
│  EfficientNetV2Classifier (V2B0)         │
└─────────────────────────────────────────┘
```

### Flujo de datos

```
MainActivity (selectedModel, benchmarkMetrics)
    ├── HomeScreen ─── toggle ModelType
    ├── TakePhotoScreen ─── image captured
    ├── AnalyzingScreen
    │     └── AnalysisViewModel.startAnalysis(imagePath)
    │           └── ClassifierFactory.getOrCreate(selectedModel)
    │                 └── BenchmarkClassifier.classify(bitmap)
    │                       └── PlantDiseaseClassifier / EfficientNetV2Classifier
    │                 └── BenchmarkClassifier.getMetrics() → BenchmarkMetrics
    │           └── AnalysisState.Success(result, metrics)
    └── ResultScreen(result, benchmarkMetrics)
```

### Archivos clave con rutas

| Responsabilidad | Archivo |
|---|---|
| Navegación principal | `com/leafdisease/MainActivity.kt` |
| Pantalla inicio + selector modelo | `com/leafdisease/view/HomeScreen.kt` |
| Pantalla de análisis | `com/leafdisease/view/AnalyzingScreen.kt` |
| Pantalla resultados + métricas | `com/leafdisease/view/ResultScreen.kt` |
| ViewModel del análisis | `com/leafdisease/viewmodel/AnalysisViewModel.kt` |
| Classifier MobileNetV3 | `com/leafdisease/ml/PlantDiseaseClassifier.kt` |
| Classifier EfficientNetV2B0 | `com/leafdisease/ml/EfficientNetV2Classifier.kt` |
| Wrapper de métricas | `com/leafdisease/ml/BenchmarkClassifier.kt` |
| Fábrica de classifiers | `com/leafdisease/ml/ClassifierFactory.kt` |
| Enum de modelos | `com/leafdisease/domain/model/ModelType.kt` |
| Data class de métricas | `com/leafdisease/domain/model/BenchmarkMetrics.kt` |
| Base de datos de enfermedades | `com/leafdisease/domain/model/ClassificationResult.kt` |
| Assets (modelos TFLite) | `app/src/main/assets/` |

---

## Referencias

- [LiteRT para Android](https://ai.google.dev/edge/litert/android)
- [Searching for MobileNetV3 (Howard et al., 2019)](https://arxiv.org/abs/1905.02244)
- [EfficientNetV2: Smaller Models and Faster Training (Tan y Le, 2021)](https://arxiv.org/abs/2104.00298)
- [PlantVillage Dataset](https://github.com/spMohanty/PlantVillage-Dataset)
- [CameraX Documentation](https://developer.android.com/training/camerax)

---

## Autoría

Aplicación desarrollada por Stefany Carolina Chávez David como parte de la tesis de Maestría en Ingeniería de Software,
Facultad de Informática, Universidad Nacional de La Plata. La aplicación se utilizó para evaluar la
ejecución en dispositivo de los modelos MobileNetV3 Small y EfficientNetV2B0 en la clasificación de
enfermedades foliares.

