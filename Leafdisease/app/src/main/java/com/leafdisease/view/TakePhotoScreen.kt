package com.leafdisease.view

import android.content.Context
import android.util.Log
import android.view.ViewTreeObserver
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.leafdisease.R
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.components.ScreenHeader
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.Preview as CameraPreview

@Composable
fun TakePhotoScreen(
    onBackClick: () -> Unit = {},
    onPhotoTaken: (String, Int) -> Unit = { _, _ -> },  // Pass path AND rotation
    onGalleryClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLoading by remember { mutableStateOf(true) }
    var isCapturing by remember { mutableStateOf(false) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Cleanup executor on dispose
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LeafDiseaseTheme.colors.background)
            .statusBarsPadding()
    ) {
        // Header
        ScreenHeader(
            text = stringResource(R.string.take_photo_title),
            icon = R.drawable.ic_round_arrow_left_24,
            onIconClicked = onBackClick
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Camera Preview Container with Focus Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension16)
                .weight(1f)
                .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension24))
                .border(
                    width = LeafDiseaseTheme.dimensions.dimension4,
                    color = LeafDiseaseTheme.colors.primary,
                    shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension24)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Camera Preview
            var previewView by remember { mutableStateOf<PreviewView?>(null) }

            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    previewView = view
                }
            )

            // Bind camera when previewView is ready
            previewView?.let { view ->
                CameraBinder(
                    previewView = view,
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onImageCaptureReady = { capture ->
                        imageCapture = capture
                        isLoading = false
                    },
                    onError = {
                        isLoading = false
                    }
                )
            }

            // Focus Overlay (Scanner Corner Brackets)
            val cornerColor = LeafDiseaseTheme.colors.primary
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawScannerOverlay(cornerColor)
                    }
            )

            // Center Focus Icon
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension48),
                    painter = painterResource(R.drawable.ic_center_focus_strong_24),
                    tint = Color.White.copy(alpha = 0.8f),
                    contentDescription = stringResource(R.string.focus_area_description)
                )
                Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension8))
                Text(
                    text = stringResource(R.string.focus_instruction),
                    style = LeafDiseaseTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }

            // Loading Indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LeafDiseaseTheme.colors.primary
                    )
                }
            }

            // Capture Flash Effect
            if (isCapturing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Control Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension32),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery Button
            IconButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .size(LeafDiseaseTheme.dimensions.dimension56)
                    .clip(CircleShape)
                    .background(LeafDiseaseTheme.colors.surface)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_image_24),
                    contentDescription = stringResource(R.string.open_gallery),
                    tint = LeafDiseaseTheme.colors.onSurface,
                    modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension28)
                )
            }

            // Capture Button
            IconButton(
                onClick = {
                    if (!isCapturing && imageCapture != null) {
                        isCapturing = true
                        val rotation = imageCapture?.targetRotation ?: 0
                        takePhoto(
                            imageCapture = imageCapture!!,
                            context = context,
                            rotation = rotation,
                            executor = cameraExecutor,
                            onImageCaptured = { path, rot ->
                                isCapturing = false
                                onPhotoTaken(path, rot)
                            },
                            onError = {
                                isCapturing = false
                            }
                        )
                    }
                },
                modifier = Modifier
                    .size(LeafDiseaseTheme.dimensions.dimension80)
                    .clip(CircleShape)
                    .background(LeafDiseaseTheme.colors.primary)
                    .border(
                        width = 4.dp,
                        color = LeafDiseaseTheme.colors.surface,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_round_camera_alt_24),
                    contentDescription = stringResource(R.string.take_photo_button),
                    tint = LeafDiseaseTheme.colors.onPrimary,
                    modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension36)
                )
            }

            // Spacer for symmetry (can be replaced with settings or flash toggle in the future)
            Spacer(modifier = Modifier.width(LeafDiseaseTheme.dimensions.dimension56))
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Bottom Instructions
        Text(
            text = stringResource(R.string.take_photo_instructions),
            style = LeafDiseaseTheme.customTypography.subtitle,
            color = LeafDiseaseTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension32)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))
    }
}

@Composable
private fun CameraBinder(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onError: () -> Unit
) {
    DisposableEffect(previewView) {
        var cameraProvider: ProcessCameraProvider? = null
        var isBound = false

        val startCamera = {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    try {
                        cameraProvider = cameraProviderFuture.get()

                        // Preview use case
                        val preview = CameraPreview.Builder()
                            .build()
                            .also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                        // Image capture use case
                        val imageCapture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            // This sets the ideal size for the image to be analyse, CameraX will choose the
                            // the most suitable resolution which may not be exactly the same or hold the same
                            // aspect ratio
                            .setTargetResolution(android.util.Size(224, 224))
                            .build()

                        // Select back camera
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        // Unbind all use cases before rebinding
                        cameraProvider?.unbindAll()

                        // Bind use cases to camera
                        cameraProvider?.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )

                        isBound = true
                        onImageCaptureReady(imageCapture)
                    } catch (e: Exception) {
                        Log.e("TakePhotoScreen", "Use case binding failed", e)
                        onError()
                    }
                }, ContextCompat.getMainExecutor(context))
            } catch (e: Exception) {
                Log.e("TakePhotoScreen", "Failed to start camera", e)
                onError()
            }
        }

        // Wait for the view to be laid out before starting camera
        if (previewView.width > 0 && previewView.height > 0) {
            startCamera()
        } else {
            val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    previewView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    if (previewView.width > 0 && previewView.height > 0) {
                        startCamera()
                    } else {
                        onError()
                    }
                }
            }
            previewView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        }

        onDispose {
            if (isBound) {
                try {
                    cameraProvider?.unbindAll()
                } catch (e: Exception) {
                    Log.e("TakePhotoScreen", "Failed to unbind camera", e)
                }
            }
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    rotation: Int,
    executor: ExecutorService,
    onImageCaptured: (String, Int) -> Unit,
    onError: () -> Unit
) {
    // Create output directory
    val outputDirectory = File(context.cacheDir, "camera_images").apply {
        if (!exists()) mkdirs()
    }

    // Create output file
    val photoFile = File(
        outputDirectory,
        "IMG_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageCaptured(photoFile.absolutePath, rotation)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("TakePhotoScreen", "Photo capture failed", exception)
                onError()
            }
        }
    )
}

/**
 * Draws a scanner-style overlay with corner brackets and semi-transparent dark overlay
 */
private fun DrawScope.drawScannerOverlay(cornerColor: Color) {
    val overlayColor = Color.Black.copy(alpha = 0.5f)
    val cornerLength = 80.dp.toPx()
    val cornerWidth = 4.dp.toPx()

    // Calculate the focus area (center 70% of the preview)
    val focusAreaSize = Size(size.width * 0.7f, size.height * 0.6f)
    val focusAreaLeft = (size.width - focusAreaSize.width) / 2
    val focusAreaTop = (size.height - focusAreaSize.height) / 2
    val focusArea = Rect(
        offset = Offset(focusAreaLeft, focusAreaTop),
        size = focusAreaSize
    )

    // Draw semi-transparent overlay
    drawRect(color = overlayColor)

    // Clear the focus area (using BlendMode.Clear)
    drawRect(
        color = Color.Transparent,
        topLeft = focusArea.topLeft,
        size = focusArea.size,
        blendMode = BlendMode.Clear
    )

    // Draw corner brackets
    // Top-left corner
    drawLine(
        color = cornerColor,
        start = Offset(focusAreaLeft, focusAreaTop + cornerLength),
        end = Offset(focusAreaLeft, focusAreaTop),
        strokeWidth = cornerWidth
    )
    drawLine(
        color = cornerColor,
        start = Offset(focusAreaLeft, focusAreaTop),
        end = Offset(focusAreaLeft + cornerLength, focusAreaTop),
        strokeWidth = cornerWidth
    )

    // Top-right corner
    val rightX = focusAreaLeft + focusAreaSize.width
    drawLine(
        color = cornerColor,
        start = Offset(rightX - cornerLength, focusAreaTop),
        end = Offset(rightX, focusAreaTop),
        strokeWidth = cornerWidth
    )
    drawLine(
        color = cornerColor,
        start = Offset(rightX, focusAreaTop),
        end = Offset(rightX, focusAreaTop + cornerLength),
        strokeWidth = cornerWidth
    )

    // Bottom-left corner
    val bottomY = focusAreaTop + focusAreaSize.height
    drawLine(
        color = cornerColor,
        start = Offset(focusAreaLeft, bottomY - cornerLength),
        end = Offset(focusAreaLeft, bottomY),
        strokeWidth = cornerWidth
    )
    drawLine(
        color = cornerColor,
        start = Offset(focusAreaLeft, bottomY),
        end = Offset(focusAreaLeft + cornerLength, bottomY),
        strokeWidth = cornerWidth
    )

    // Bottom-right corner
    drawLine(
        color = cornerColor,
        start = Offset(rightX - cornerLength, bottomY),
        end = Offset(rightX, bottomY),
        strokeWidth = cornerWidth
    )
    drawLine(
        color = cornerColor,
        start = Offset(rightX, bottomY - cornerLength),
        end = Offset(rightX, bottomY),
        strokeWidth = cornerWidth
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun TakePhotoScreenPreview() {
    LeafDiseaseTheme {
        TakePhotoScreen()
    }
}
