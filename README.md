# Leaf-disease-applicaton

## 1. Descripción del proyecto

**Nombre:** Leaf Disease Detection
Aplicación Android nativa que clasifica enfermedades en hojas de plantas usando modelos de aprendizaje profundo corriendo directamente en el dispositivo (on-device inference), sin dependencia de servidores externos.

**Stack tecnológico:**
| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin 100% |
| UI | Jetpack Compose (sin layouts XML) |
| Inference engine | Google AI Edge LiteRT (`CompiledModel`) |
| Cámara | CameraX |
| Inyección de dependencias | Dagger 2 + KSP |
| Arquitectura | MVVM (Model-View-ViewModel) |
| Async | Kotlin Coroutines + StateFlow |

**Versión mínima Android:** API 24 (Android 7.0)
**Target SDK:** 36

---

## 2. Arquitectura actual

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

- [TensorFlow Lite Android Guide](https://www.tensorflow.org/lite/android)
- [MobileNetV2 Paper](https://arxiv.org/abs/1801.04381)
- [PlantVillage Dataset](https://github.com/spMohanty/PlantVillage-Dataset)
- [CameraX Documentation](https://developer.android.com/training/camerax)

---

## Autoría

Aplicación desarrollada por Stefany Carolina Chávez David como parte de la tesis de Maestría en Ingeniería de Software,
Facultad de Informática, Universidad Nacional de La Plata. La aplicación se utilizó para evaluar la
ejecución en dispositivo de los modelos MobileNetV3 Small y EfficientNetV2B0 en la clasificación de
enfermedades foliares.

