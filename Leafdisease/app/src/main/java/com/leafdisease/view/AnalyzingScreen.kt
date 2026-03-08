package com.leafdisease.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leafdisease.R
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.components.CoreButton
import com.leafdisease.view.components.CoreButtonType
import com.leafdisease.view.components.ScreenHeader
import com.leafdisease.viewmodel.AnalysisState
import com.leafdisease.viewmodel.AnalysisViewModel
import java.io.File

@Composable
fun AnalyzingScreen(
    imagePath: String,
    onCancelClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onAnalysisComplete: (Boolean, String?) -> Unit = { _, _ -> },
    viewModel: AnalysisViewModel = viewModel(factory = AnalysisViewModel.Factory(androidx.compose.ui.platform.LocalContext.current))
) {
    val analysisState by viewModel.analysisState.collectAsState()

    // Initialize and start analysis
    LaunchedEffect(imagePath) {
        viewModel.initializeClassifier()
        viewModel.analyzeImage(imagePath)
    }

    // Handle state changes
    LaunchedEffect(analysisState) {
        when (val state = analysisState) {
            is AnalysisState.Success -> {
                // Navigate to result screen with the classification result
                onAnalysisComplete(true, state.result.diseaseName)
            }
            is AnalysisState.Error -> {
                // Could show error dialog or retry option
            }
            else -> { /* Continue showing progress */ }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LeafDiseaseTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        ScreenHeader(
            text = stringResource(R.string.analyzing_screen_title),
            icon = R.drawable.ic_round_arrow_left_24,
            onIconClicked = onBackClick
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Plant Image with Scanning Overlay
        val bitmap = rememberImageBitmap(imagePath)
        PlantScanningImage(
            bitmap = bitmap,
            modifier = Modifier
                .size(LeafDiseaseTheme.dimensions.dimension180)
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Status Icon based on state
        val (iconRes, iconTint) = when (analysisState) {
            is AnalysisState.Success -> R.drawable.ic_leaf_24 to LeafDiseaseTheme.colors.primary
            is AnalysisState.Error -> R.drawable.ic_close_24 to LeafDiseaseTheme.colors.error
            else -> R.drawable.ic_leaf_24 to LeafDiseaseTheme.colors.primary
        }

        Box(
            modifier = Modifier
                .size(LeafDiseaseTheme.dimensions.dimension64)
                .clip(CircleShape)
                .background(LeafDiseaseTheme.colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension32),
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension16))

        // Dynamic Title based on state
        val titleText = when (analysisState) {
            is AnalysisState.LoadingModel -> "Cargando modelo..."
            is AnalysisState.Preprocessing -> "Preprocesando imagen..."
            is AnalysisState.Analyzing -> stringResource(R.string.analyzing_title)
            is AnalysisState.Success -> "Análisis completado"
            is AnalysisState.Error -> "Error en el análisis"
            else -> stringResource(R.string.analyzing_title)
        }

        Text(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            text = titleText,
            style = LeafDiseaseTheme.customTypography.headerMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension8))

        // Dynamic Subtitle
        val subtitleText = when (analysisState) {
            is AnalysisState.LoadingModel -> "Inicializando modelo MobileNetV2..."
            is AnalysisState.Preprocessing -> "Optimizando imagen para análisis..."
            is AnalysisState.Analyzing -> stringResource(R.string.analyzing_subtitle)
            is AnalysisState.Success -> "Resultado listo para mostrar"
            is AnalysisState.Error -> (analysisState as AnalysisState.Error).message
            else -> stringResource(R.string.analyzing_subtitle)
        }

        Text(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            text = subtitleText,
            style = LeafDiseaseTheme.customTypography.subtitleMedium,
            textAlign = TextAlign.Center,
            color = LeafDiseaseTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Progress Section with dynamic progress
        val (currentProgress, progressLabel) = when (val state = analysisState) {
            is AnalysisState.Preprocessing -> {
                val progress = state.progress / 100f
                progress to "Preprocesando..."
            }
            is AnalysisState.Analyzing -> {
                val progress = state.progress / 100f
                progress to "Analizando..."
            }
            is AnalysisState.Success -> 1f to "Completo"
            is AnalysisState.Error -> 0f to "Error"
            else -> 0f to "Iniciando..."
        }

        ProgressSection(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            label = progressLabel,
            progress = currentProgress.coerceIn(0f, 1f),
            progressText = "${(currentProgress * 100).toInt()}%"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Cancel Button (only show when not complete)
        if (analysisState !is AnalysisState.Success && analysisState !is AnalysisState.Error) {
            CoreButton(
                modifier = Modifier
                    .padding(bottom = LeafDiseaseTheme.dimensions.dimension16)
                    .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
                buttonText = stringResource(R.string.analyzing_cancel_button),
                buttonImage = R.drawable.ic_close_24,
                buttonType = CoreButtonType.CANCEL,
                onClick = {
                    viewModel.reset()
                    onCancelClick()
                }
            )
        }

        // Retry button on error
        if (analysisState is AnalysisState.Error) {
            CoreButton(
                modifier = Modifier
                    .padding(bottom = LeafDiseaseTheme.dimensions.dimension16)
                    .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
                buttonText = "Reintentar",
                buttonImage = R.drawable.ic_round_camera_alt_24,
                buttonType = CoreButtonType.PRIMARY,
                onClick = { viewModel.retry() }
            )
        }
    }
}

@Composable
private fun rememberImageBitmap(imagePath: String): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val file = File(imagePath)
        if (file.exists()) {
            BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
        } else null
    } catch (e: Exception) {
        null
    }
}

@Composable
fun PlantScanningImage(
    bitmap: androidx.compose.ui.graphics.ImageBitmap?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = LeafDiseaseTheme.dimensions.dimension16,
                shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension16)
            )
            .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension16))
    ) {
        // Show actual image or placeholder
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Plant being analyzed",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(R.drawable.plant_image_soy),
                contentDescription = "Plant being analyzed",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Corner Brackets Overlay
        ScanningOverlay(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ScanningOverlay(modifier: Modifier = Modifier) {
    val cornerColor = LeafDiseaseTheme.colors.primary
    val cornerSize = LeafDiseaseTheme.dimensions.dimension24
    val cornerThickness = LeafDiseaseTheme.dimensions.dimension3

    Box(modifier = modifier) {
        // Top Left Corner
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(LeafDiseaseTheme.dimensions.dimension16)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
            )
        }

        // Top Right Corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(LeafDiseaseTheme.dimensions.dimension16)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
                    .align(Alignment.TopEnd)
            )
        }

        // Bottom Left Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(LeafDiseaseTheme.dimensions.dimension16)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
                    .align(Alignment.BottomStart)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
                    .align(Alignment.BottomStart)
            )
        }

        // Bottom Right Corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(LeafDiseaseTheme.dimensions.dimension16)
        ) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
                    .align(Alignment.BottomEnd)
            )
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun ProgressSection(
    label: String,
    progress: Float,
    progressText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = LeafDiseaseTheme.typography.labelLarge,
                color = LeafDiseaseTheme.colors.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = progressText,
                style = LeafDiseaseTheme.typography.labelMedium,
                color = LeafDiseaseTheme.colors.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension8))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(LeafDiseaseTheme.dimensions.dimension6)
                .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension3)),
            color = LeafDiseaseTheme.colors.primary,
            trackColor = LeafDiseaseTheme.colors.surfaceVariant,
        )
    }
}

@Preview
@Composable
fun AnalyzingScreenPreview() {
    LeafDiseaseTheme {
        AnalyzingScreen(
            imagePath = "",
            onCancelClick = {},
            onBackClick = {}
        )
    }
}
