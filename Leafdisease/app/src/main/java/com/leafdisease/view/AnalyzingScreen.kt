package com.leafdisease.view

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.leafdisease.R
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.components.CoreButton
import com.leafdisease.view.components.CoreButtonType
import com.leafdisease.view.components.ScreenHeader

@Composable
fun AnalyzingScreen(
    onCancelClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LeafDiseaseTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar

        ScreenHeader(
            text = stringResource(R.string.analyzing_screen_title),
            image = R.drawable.ic_round_arrow_left_24,
            onIconClicked = {}
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Plant Image with Scanning Overlay
        PlantScanningImage(
            modifier = Modifier
                .size(LeafDiseaseTheme.dimensions.dimension180)
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Leaf Icon with Circle Background
        Box(
            modifier = Modifier.Companion
                .size(LeafDiseaseTheme.dimensions.dimension64)
                .clip(CircleShape)
                .background(LeafDiseaseTheme.colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_leaf_24),
                contentDescription = null,
                modifier = Modifier.Companion.size(LeafDiseaseTheme.dimensions.dimension32),
                tint = LeafDiseaseTheme.colors.primary
            )
        }

        Spacer(modifier = Modifier.Companion.height(LeafDiseaseTheme.dimensions.dimension16))

        // Analyzing Title
        Text(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            text = stringResource(R.string.analyzing_title),
            style = LeafDiseaseTheme.customTypography.headerMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.Companion.height(LeafDiseaseTheme.dimensions.dimension8))

        // Analyzing Subtitle
        Text(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            text = stringResource(R.string.analyzing_subtitle),
            style = LeafDiseaseTheme.customTypography.subtitleMedium,
            textAlign = TextAlign.Center,
            color = LeafDiseaseTheme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Progress Section
        ProgressSection(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            label = stringResource(R.string.analyzing_step_species),
            progress = 0.74f,
            progressText = "74%"
        )


        Spacer(modifier = Modifier.weight(1f))

        // Cancel Button
        CoreButton(
            modifier = Modifier
                .padding(bottom = LeafDiseaseTheme.dimensions.dimension16)
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            buttonText = stringResource(R.string.analyzing_cancel_button),
            buttonImage = R.drawable.ic_close_24,
            buttonType = CoreButtonType.CANCEL,
            onClick = onCancelClick
        )
    }
}

@Composable
fun PlantScanningImage(
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
        // Plant Image
        Image(
            painter = painterResource(R.drawable.plant_image_soy),
            contentDescription = "Plant being analyzed",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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
            // Top horizontal line
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
            )
            // Left vertical line
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
            // Top horizontal line
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
            )
            // Right vertical line
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
            // Bottom horizontal line
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
                    .align(Alignment.BottomStart)
            )
            // Left vertical line
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
            // Bottom horizontal line
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(cornerColor)
                    .align(Alignment.BottomEnd)
            )
            // Right vertical line
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

        Spacer(modifier = Modifier.Companion.height(LeafDiseaseTheme.dimensions.dimension8))

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

@Composable
fun AnalysisStepItem(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension12))
            .background(LeafDiseaseTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(LeafDiseaseTheme.dimensions.dimension16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.Companion.size(LeafDiseaseTheme.dimensions.dimension20),
            color = LeafDiseaseTheme.colors.primary,
            strokeWidth = LeafDiseaseTheme.dimensions.dimension2
        )

        Spacer(modifier = Modifier.Companion.width(LeafDiseaseTheme.dimensions.dimension12))

        Text(
            text = text,
            style = LeafDiseaseTheme.typography.bodyMedium,
            color = LeafDiseaseTheme.colors.onSurface
        )
    }
}

@Preview
@Composable
fun AnalyzingScreenPreview() {
    LeafDiseaseTheme {
        AnalyzingScreen()
    }
}
