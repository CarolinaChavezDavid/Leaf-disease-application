package com.leafdisease

import android.Manifest
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.AnalyzingScreen
import com.leafdisease.view.HomeScreen
import com.leafdisease.view.TakePhotoScreen
import com.leafdisease.view.ResultScreen
import com.leafdisease.viewmodel.AnalysisViewModel
import java.io.File

const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf(Screen.HOME)
    private var capturedImagePath by mutableStateOf<String?>(null)
    private var classificationResult by mutableStateOf<ClassificationResult?>(null)
    private val analysisViewModelFactory: AnalysisViewModel.Factory by lazy {
        (application as LeafDiseaseApplication).appComponent.analysisViewModelFactory()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navigateTo(Screen.TAKE_PHOTO)
        } else {
            // Permission denied, stay on home screen
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult

        val cachedImagePath = copyImageToCache(uri) ?: run {
            Log.e(TAG, "Failed to import gallery image: $uri")
            return@registerForActivityResult
        }

        Log.i(TAG, "Gallery image selected: $cachedImagePath")
        capturedImagePath = cachedImagePath
        navigateTo(Screen.ANALYZING)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        configureSystemBars()
        setContent {
            LeafDiseaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        currentScreen = currentScreen,
                        capturedImagePath = capturedImagePath,
                        classificationResult = classificationResult,
                        analysisViewModelFactory = analysisViewModelFactory,
                        onUploadImage = { pickImageLauncher.launch("image/*") },
                        onNavigate = { screen ->
                            when (screen) {
                                Screen.TAKE_PHOTO -> checkCameraPermissionAndNavigate()
                                else -> navigateTo(screen)
                            }
                        },
                        onImageCaptured = { path ->
                            Log.i(TAG, "Image captured: $path")
                            capturedImagePath = path
                            navigateTo(Screen.ANALYZING)
                        },
                        onAnalysisComplete = { result ->
                            classificationResult = result
                            navigateTo(Screen.RESULT)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkCameraPermissionAndNavigate() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateTo(Screen.TAKE_PHOTO)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    private fun configureSystemBars() {
        val window = window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
    }
}

enum class Screen {
    HOME, TAKE_PHOTO, ANALYZING, RESULT
}

@Composable
fun AppNavigation(
    currentScreen: Screen,
    capturedImagePath: String?,
    classificationResult: ClassificationResult?,
    analysisViewModelFactory: AnalysisViewModel.Factory,
    onUploadImage: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onImageCaptured: (String) -> Unit,
    onAnalysisComplete: (ClassificationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = currentScreen,
        modifier = modifier,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width } + fadeOut())
            } else {
                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> width } + fadeOut())
            }
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onTakePhoto = { onNavigate(Screen.TAKE_PHOTO) },
                onUploadImage = onUploadImage
            )
            Screen.TAKE_PHOTO -> TakePhotoScreen(
                onBackClick = { onNavigate(Screen.HOME) },
                onPhotoTaken = onImageCaptured,
                onGalleryClick = { /* Handle gallery click */ }
            )
            Screen.ANALYZING -> AnalyzingScreen(
                imagePath = capturedImagePath ?: "",
                viewModelFactory = analysisViewModelFactory,
                onCancelClick = { onNavigate(Screen.HOME) },
                onBackClick = { onNavigate(Screen.TAKE_PHOTO) },
                onAnalysisComplete = { success, result ->
                    if (success && result != null) {
                        onAnalysisComplete(result)
                    }
                }
            )
            Screen.RESULT -> ResultScreen(
                imagePath = capturedImagePath,
                result = classificationResult ?: createMockResult(),
                onBackClick = { onNavigate(Screen.HOME) },
                onScanAnotherClick = { onNavigate(Screen.HOME) }
            )
        }
    }
}

private fun MainActivity.copyImageToCache(uri: Uri): String? {
    return runCatching {
        val outputDirectory = File(cacheDir, "gallery_images").apply {
            if (!exists()) mkdirs()
        }

        val mimeType = contentResolver.getType(uri).orEmpty()
        val extension = when {
            "png" in mimeType -> "png"
            "webp" in mimeType -> "webp"
            else -> "jpg"
        }

        val outputFile = File(
            outputDirectory,
            "IMG_${System.currentTimeMillis()}.$extension"
        )

        contentResolver.openInputStream(uri)?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null

        outputFile.absolutePath
    }.getOrNull()
}

// Fallback mock result for preview/error cases
private fun createMockResult(): ClassificationResult {
    return ClassificationResult(
        diseaseName = "Unknown",
        scientificName = null,
        confidence = 0f,
        isHealthy = true,
        description = "No analysis result available.",
        treatmentRecommendations = emptyList(),
        confidenceLevel = ClassificationResult.ConfidenceLevel.LOW,
        plantType = "Unknown"
    )
}
