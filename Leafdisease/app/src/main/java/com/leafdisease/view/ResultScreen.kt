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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.leafdisease.R
import com.leafdisease.domain.model.ClassificationResult
import com.leafdisease.domain.model.TreatmentStep
import com.leafdisease.ui.format.toPercentageString
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.components.CoreButton
import com.leafdisease.view.components.CoreButtonType
import com.leafdisease.view.components.ScreenHeader

@Composable
fun ResultScreen(
    imagePath: String?,
    onBackClick: () -> Unit = {},
    onScanAnotherClick: () -> Unit = {},
    result: ClassificationResult
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LeafDiseaseTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar

        ScreenHeader(
            text = stringResource(R.string.result_screen_title),
            image = R.drawable.ic_round_arrow_left_24,
            onIconClicked = { onBackClick.invoke() }
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Plant Image Card
        PlantResultImage(
            imagePath = imagePath,
            result = result,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Diagnosis Section
        DiagnosisSection(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            result = result
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Action Items Section
        ActionItemsSection(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24),
            result = result
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Bottom Button
        CoreButton(
            modifier = Modifier
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
                .padding(bottom = LeafDiseaseTheme.dimensions.dimension16),
            buttonText = stringResource(R.string.result_scan_another_button),
            buttonImage = R.drawable.ic_round_camera_alt_24,
            buttonType = CoreButtonType.PRIMARY,
            onClick = onScanAnotherClick
        )
    }
}

@Composable
fun PlantResultImage(
    imagePath: String?,
    result: ClassificationResult,
    modifier: Modifier = Modifier
) {
    val imageBitmap = rememberResultImageBitmap(imagePath)

    Card(
        modifier = modifier
            .height(LeafDiseaseTheme.dimensions.dimension200)
            .shadow(
                elevation = LeafDiseaseTheme.dimensions.dimension16,
                shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension16)
            ),
        shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension16),
        colors = CardDefaults.cardColors(
            containerColor = LeafDiseaseTheme.colors.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Plant Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_center_focus_strong_24),
                    contentDescription = "Plant Image",
                    modifier = Modifier.fillMaxSize(),
                    tint = LeafDiseaseTheme.colors.primary
                )
            }

            // Label Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(LeafDiseaseTheme.dimensions.dimension12)
                    .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension8))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = LeafDiseaseTheme.dimensions.dimension8, vertical = LeafDiseaseTheme.dimensions.dimension4)
            ) {
                Text(
                    text = result.plantType,
                    style = LeafDiseaseTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun rememberResultImageBitmap(imagePath: String?): ImageBitmap? {
    return remember(imagePath) {
        imagePath
            ?.takeIf { it.isNotBlank() }
            ?.let { path -> BitmapFactory.decodeFile(path)?.asImageBitmap() }
    }
}

@Composable
fun DiagnosisSection(
    modifier: Modifier = Modifier,
    result: ClassificationResult
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Diagnosis Label
            Text(
                text = stringResource(R.string.result_diagnosis_label),
                style = LeafDiseaseTheme.typography.labelSmall,
                color = LeafDiseaseTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension4))

            // Disease Name
            Text(
                text = result.diseaseName,
                style = LeafDiseaseTheme.customTypography.header,
                color = LeafDiseaseTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension4))

            // Scientific Name

            Text(
                text = result.scientificName ?: result.description,
                style = LeafDiseaseTheme.typography.bodyMedium,
                color = LeafDiseaseTheme.colors.onSurfaceVariant
            )
        }

        // Confidence Indicator
        ConfidenceIndicator(
            result = result,
            modifier = Modifier.padding(start = LeafDiseaseTheme.dimensions.dimension16)
        )
    }
}

@Composable
fun ConfidenceIndicator(
    result: ClassificationResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension68),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { result.confidence },
                modifier = Modifier.fillMaxSize(),
                color = LeafDiseaseTheme.colors.primary,
                trackColor = LeafDiseaseTheme.colors.surfaceVariant,
                strokeWidth = LeafDiseaseTheme.dimensions.dimension4
            )
            Text(
                text = result.confidence.toPercentageString(),
                style = LeafDiseaseTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = LeafDiseaseTheme.colors.primary
            )
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension4))

        if (!result.isHealthy) {
            Text(
                text = stringResource(R.string.result_infected_status),
                style = LeafDiseaseTheme.typography.labelSmall,
                color = LeafDiseaseTheme.colors.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActionItemsSection(
    modifier: Modifier = Modifier,
    result: ClassificationResult
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Title with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_leaf_24),
                contentDescription = null,
                tint = LeafDiseaseTheme.colors.primary,
                modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension24)
            )

            Spacer(modifier = Modifier.width(LeafDiseaseTheme.dimensions.dimension8))

            Text(
                text = stringResource(R.string.result_action_title),
                style = LeafDiseaseTheme.typography.titleMedium,
                color = LeafDiseaseTheme.colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension16))

        result.treatmentRecommendations.forEachIndexed { index, treatmentStep ->
            ActionItem(
                icon = R.drawable.ic_baseline_image_24,
                title = "${treatmentStep.stepNumber}. ${treatmentStep.title}",
                description = treatmentStep.description
            )

            if (index < result.treatmentRecommendations.size - 1) {
                Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension12))
            }
        }
    }
}

@Composable
fun ActionItem(
    icon: Int,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension12),
        colors = CardDefaults.cardColors(
            containerColor = LeafDiseaseTheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = LeafDiseaseTheme.dimensions.dimension2
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LeafDiseaseTheme.dimensions.dimension16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(LeafDiseaseTheme.dimensions.dimension40)
                    .clip(CircleShape)
                    .background(LeafDiseaseTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = LeafDiseaseTheme.colors.primary,
                    modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension24)
                )
            }

            Spacer(modifier = Modifier.width(LeafDiseaseTheme.dimensions.dimension12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LeafDiseaseTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = LeafDiseaseTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension2))

                Text(
                    text = description,
                    style = LeafDiseaseTheme.typography.bodySmall,
                    color = LeafDiseaseTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EncyclopediaLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension12))
            .background(LeafDiseaseTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(LeafDiseaseTheme.dimensions.dimension16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leaf/Book Icon
            Icon(
                painter = painterResource(R.drawable.ic_leaf_24),
                contentDescription = null,
                tint = LeafDiseaseTheme.colors.primary,
                modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension24)
            )

            Spacer(modifier = Modifier.width(LeafDiseaseTheme.dimensions.dimension12))

            Text(
                text = stringResource(R.string.result_encyclopedia_link),
                style = LeafDiseaseTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = LeafDiseaseTheme.colors.onBackground
            )
        }

        // Arrow Icon
        Icon(
            painter = painterResource(R.drawable.ic_round_arrow_back_24),
            contentDescription = "Navigate",
            tint = LeafDiseaseTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension20)
        )
    }
}

@Preview
@Composable
fun ResultScreenPreview() {
    val mockResult = ClassificationResult(
        diseaseName = "Tomato Late Blight",
        scientificName = "Phytophthora infestans",
        confidence = 0.92f,
        isHealthy = false,
        description = "Devastating fungal disease causing brown lesions and white fungal growth on leaves and fruit. Requires immediate treatment to prevent total crop loss.",
        treatmentRecommendations = listOf(
            TreatmentStep(
                stepNumber = 1,
                title = "Remove infected plants",
                description = "Destroy all infected plants immediately to prevent spread to healthy plants.",
                priority = TreatmentStep.Priority.HIGH
            ),
            TreatmentStep(
                stepNumber = 2,
                title = "Apply fungicide",
                description = "Use mancozeb or chlorothalonil urgently. Apply every 7-10 days during wet weather.",
                priority = TreatmentStep.Priority.HIGH
            ),
            TreatmentStep(
                stepNumber = 3,
                title = "Space plants",
                description = "Improve air circulation around plants by pruning lower leaves and ensuring proper spacing.",
                priority = TreatmentStep.Priority.MEDIUM
            )
        ),
        confidenceLevel = ClassificationResult.ConfidenceLevel.HIGH,
        plantType = "Tomato"
    )

    LeafDiseaseTheme {
        ResultScreen(
            imagePath = "",
            result = mockResult,
            onBackClick = {},
            onScanAnotherClick = {}
        )
    }
}
