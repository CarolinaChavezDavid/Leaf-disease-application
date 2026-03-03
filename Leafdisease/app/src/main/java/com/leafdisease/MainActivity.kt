package com.leafdisease

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.AnalyzingScreen
import com.leafdisease.view.HomeScreen
import com.leafdisease.view.TakePhotoScreen
import com.leafdisease.view.ResultScreen

class MainActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf(Screen.HOME)
    private var capturedImagePath by mutableStateOf<String?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, navigate to camera
            navigateTo(Screen.TAKE_PHOTO)
        } else {
            // Permission denied, stay on home screen
            // Could show a toast or dialog here
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeafDiseaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            when (screen) {
                                Screen.TAKE_PHOTO -> checkCameraPermissionAndNavigate()
                                else -> navigateTo(screen)
                            }
                        },
                        onImageCaptured = { path ->
                            capturedImagePath = path
                            navigateTo(Screen.ANALYZING)
                        },
                        modifier = Modifier.padding(innerPadding)
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
}

enum class Screen {
    HOME, TAKE_PHOTO, ANALYZING, RESULT
}

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onImageCaptured: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = currentScreen,
        modifier = modifier,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                // Moving forward: slide in from right, slide out to left
                (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width } + fadeOut())
            } else {
                // Moving backward: slide in from left, slide out to right
                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> width } + fadeOut())
            }
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onTakePhoto = { onNavigate(Screen.TAKE_PHOTO) },
                onUploadImage = { /* Handle gallery upload */ }
            )
            Screen.TAKE_PHOTO -> TakePhotoScreen(
                onBackClick = { onNavigate(Screen.HOME) },
                onPhotoTaken = onImageCaptured,
                onGalleryClick = { /* Handle gallery click */ }
            )
            Screen.ANALYZING -> AnalyzingScreen(
                onCancelClick = { onNavigate(Screen.HOME) },
                onBackClick = { onNavigate(Screen.TAKE_PHOTO) }
            )
            Screen.RESULT -> ResultScreen(
                onBackClick = { onNavigate(Screen.HOME) },
                onScanAnotherClick = { onNavigate(Screen.HOME) }
            )
        }
    }
}
