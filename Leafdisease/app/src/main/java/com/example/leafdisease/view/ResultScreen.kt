package com.example.leafdisease.view

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafdisease.R
import com.example.leafdisease.ui.theme.LeafDiseaseTheme
import com.example.leafdisease.view.components.CoreButton
import com.example.leafdisease.view.components.CoreButtonType

@Composable
fun ResultScreen(
    onBackClick: () -> Unit = {},
    onScanAnotherClick: () -> Unit = {},
    onEncyclopediaClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LeafDiseaseTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension16)
                .padding(top = LeafDiseaseTheme.dimensions.dimension16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_round_arrow_back_24),
                    contentDescription = "Back",
                    tint = LeafDiseaseTheme.colors.onBackground
                )
            }
            Text(
                text = stringResource(R.string.result_screen_title),
                style = LeafDiseaseTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            // Spacer to balance the IconButton width
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Plant Image Card
        PlantResultImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Diagnosis Section
        DiagnosisSection(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension24))

        // Pathogen Tags
        PathogenTags(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension32))

        // Action Items Section
        ActionItemsSection(
            modifier = Modifier.padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
        )

        Spacer(modifier = Modifier.weight(1f))

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
    modifier: Modifier = Modifier
) {
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
            // Plant Image
            Icon(
                painter = painterResource(R.drawable.ic_center_focus_strong_24),
                contentDescription = "Plant Image",
                modifier = Modifier.fillMaxSize(),
                tint = LeafDiseaseTheme.colors.primary
            )

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
                    text = "Eupta lata Hey",
                    style = LeafDiseaseTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DiagnosisSection(
    modifier: Modifier = Modifier
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
                text = stringResource(R.string.result_disease_name),
                style = LeafDiseaseTheme.customTypography.header,
                color = LeafDiseaseTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension4))

            // Scientific Name
            Text(
                text = stringResource(R.string.result_scientific_name),
                style = LeafDiseaseTheme.typography.bodyMedium,
                color = LeafDiseaseTheme.colors.onSurfaceVariant
            )
        }

        // Confidence Indicator
        ConfidenceIndicator(
            confidence = 94,
            modifier = Modifier.padding(start = LeafDiseaseTheme.dimensions.dimension16)
        )
    }
}

@Composable
fun ConfidenceIndicator(
    confidence: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension56),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { confidence / 100f },
                modifier = Modifier.fillMaxSize(),
                color = LeafDiseaseTheme.colors.primary,
                trackColor = LeafDiseaseTheme.colors.surfaceVariant,
                strokeWidth = LeafDiseaseTheme.dimensions.dimension4
            )
            Text(
                text = "$confidence%",
                style = LeafDiseaseTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = LeafDiseaseTheme.colors.primary
            )
        }

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension4))

        Text(
            text = stringResource(R.string.result_infected_status),
            style = LeafDiseaseTheme.typography.labelSmall,
            color = LeafDiseaseTheme.colors.error,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PathogenTags(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LeafDiseaseTheme.dimensions.dimension8)
    ) {
        // Pathogen Tag (Red/Danger)
        TagChip(
            text = stringResource(R.string.result_pathogen_tag),
            backgroundColor = LeafDiseaseTheme.colors.errorContainer,
            textColor = LeafDiseaseTheme.colors.error
        )

        // Fungus Type Tag (Blue/Info)
        TagChip(
            text = stringResource(R.string.result_fungus_tag),
            backgroundColor = LeafDiseaseTheme.colors.primaryContainer,
            textColor = LeafDiseaseTheme.colors.primary
        )
    }
}

@Composable
fun TagChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension20))
            .background(backgroundColor)
            .padding(horizontal = LeafDiseaseTheme.dimensions.dimension12, vertical = LeafDiseaseTheme.dimensions.dimension6)
    ) {
        Text(
            text = text,
            style = LeafDiseaseTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionItemsSection(
    modifier: Modifier = Modifier
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

        // Action Items
        ActionItem(
            icon = R.drawable.ic_baseline_image_24,
            title = stringResource(R.string.result_action_1_title),
            description = stringResource(R.string.result_action_1_desc)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension12))

        ActionItem(
            icon = R.drawable.ic_round_camera_alt_24,
            title = stringResource(R.string.result_action_2_title),
            description = stringResource(R.string.result_action_2_desc)
        )

        Spacer(modifier = Modifier.height(LeafDiseaseTheme.dimensions.dimension12))

        ActionItem(
            icon = R.drawable.ic_center_focus_strong_24,
            title = stringResource(R.string.result_action_3_title),
            description = stringResource(R.string.result_action_3_desc)
        )
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
    LeafDiseaseTheme {
        ResultScreen()
    }
}
