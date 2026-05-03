package com.leafdisease.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.ViewModelProvider
import com.leafdisease.R
import com.leafdisease.ml.LeafDiseaseClassifier
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.components.CoreButton
import com.leafdisease.view.components.CoreButtonType
import com.leafdisease.view.components.ScreenHeader
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.viewmodel.AnalysisState
import com.leafdisease.viewmodel.AnalysisViewModel
import java.io.File

const val TAG = "AnalyzingScreen"
private const val MINIMUM_LOADER_DURATION_MS = 5_000L

@Composable
fun AnalyzingScreen(
    imagePath: String,
    viewModelFactory: ViewModelProvider.Factory,
    onCancelClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onAnalysisComplete: (Boolean, ClassificationResult?) -> Unit = { _, _ -> },
    viewModel: AnalysisViewModel = viewModel(factory = viewModelFactory)
) {
    val analysisState by viewModel.analysisState.collectAsState()
    var analysisStartedAt by remember(imagePath) {
        mutableLongStateOf(0L)
    }
    var terminalState by remember(imagePath) {
        mutableStateOf<AnalysisState?>(null)
    }

    // Initialize and start analysis
    LaunchedEffect(imagePath) {
        Log.i(TAG, "image path: $imagePath")
        viewModel.reset()
        terminalState = null
        analysisStartedAt = SystemClock.elapsedRealtime()
        viewModel.startAnalysis(imagePath)
    }

    // Handle state changes
    LaunchedEffect(analysisState, terminalState) {
        when (analysisState) {
            is AnalysisState.Success,
            is AnalysisState.Error -> terminalState = analysisState
            else -> if (terminalState != null) {
                terminalState = null
            }
        }
    }

    val currentProgress by produceState(
        initialValue = 0f,
        key1 = analysisState,
        key2 = terminalState,
        key3 = analysisStartedAt
    ) {
        while (true) {
            val elapsed = (SystemClock.elapsedRealtime() - analysisStartedAt).coerceAtLeast(0L)
            val activeState = terminalState ?: analysisState

            value = when (activeState) {
                AnalysisState.Idle -> 0f
                AnalysisState.LoadingModel -> progressFromElapsed(
                    elapsed = elapsed,
                    minValue = 0.02f,
                    maxValue = 0.12f,
                    durationMs = 1_000L
                )
                AnalysisState.Preprocessing -> progressFromElapsed(
                    elapsed = elapsed,
                    minValue = 0.12f,
                    maxValue = 0.40f,
                    durationMs = 2_000L
                )
                AnalysisState.Analyzing -> {
                    if (elapsed <= MINIMUM_LOADER_DURATION_MS) {
                        progressFromElapsed(
                            elapsed = elapsed,
                            minValue = 0.40f,
                            maxValue = 0.90f,
                            durationMs = MINIMUM_LOADER_DURATION_MS
                        )
                    } else {
                        progressFromElapsed(
                            elapsed = elapsed - MINIMUM_LOADER_DURATION_MS,
                            minValue = 0.90f,
                            maxValue = 0.98f,
                            durationMs = 10_000L
                        )
                    }
                }
                is AnalysisState.Success,
                is AnalysisState.Error -> {
                    if (elapsed >= MINIMUM_LOADER_DURATION_MS) {
                        1f
                    } else {
                        progressFromElapsed(
                            elapsed = elapsed,
                            minValue = 0.90f,
                            maxValue = 1f,
                            durationMs = MINIMUM_LOADER_DURATION_MS
                        )
                    }
                }
            }.coerceIn(0f, 1f)

            if (terminalState != null && value >= 1f) {
                break
            }
            kotlinx.coroutines.delay(16L)
        }
    }

    LaunchedEffect(currentProgress, terminalState) {
        if (currentProgress < 1f) return@LaunchedEffect

        when (val state = terminalState) {
            is AnalysisState.Success -> onAnalysisComplete(true, state.result)
            is AnalysisState.Error -> onAnalysisComplete(false, null)
            else -> Unit
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
            AnalysisState.LoadingModel -> "Cargando modelo..."
            AnalysisState.Preprocessing -> "Preprocesando imagen..."
            AnalysisState.Analyzing -> stringResource(R.string.analyzing_title)
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
            AnalysisState.LoadingModel -> "Inicializando modelo MobileNetV2..."
            AnalysisState.Preprocessing -> "Optimizando imagen para análisis..."
            AnalysisState.Analyzing -> stringResource(R.string.analyzing_subtitle)
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
        val progressLabel = when (analysisState) {
            AnalysisState.LoadingModel -> "Inicializando..."
            AnalysisState.Preprocessing -> "Preprocesando..."
            AnalysisState.Analyzing -> "Analizando..."
            is AnalysisState.Success -> "Completo"
            is AnalysisState.Error -> "Error"
            else -> "Iniciando..."
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

private fun progressFromElapsed(
    elapsed: Long,
    minValue: Float,
    maxValue: Float,
    durationMs: Long
): Float {
    if (durationMs <= 0L) return maxValue
    val normalized = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
    return minValue + ((maxValue - minValue) * normalized)
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
            viewModelFactory = AnalysisViewModel.Factory(
                object : LeafDiseaseClassifier {
                    override fun classify(bitmap: Bitmap): ClassificationResult? = null
                    override fun close() = Unit
                }
            ),
            onCancelClick = {},
            onBackClick = {}
        )
    }
}
