package com.leafdisease.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import com.leafdisease.R
import com.leafdisease.domain.model.ModelType
import com.leafdisease.ui.theme.LeafDiseaseTheme
import com.leafdisease.view.components.CoreButton
import com.leafdisease.view.components.CoreButtonType
import com.leafdisease.view.components.ScreenHeader


@Composable
fun HomeScreen(
    onTakePhoto: () -> Unit = {},
    onUploadImage: () -> Unit = {},
    selectedModel: ModelType = ModelType.MOBILENET_V2,
    onModelSelected: (ModelType) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        ScreenHeader(
            modifier = Modifier.shadow(elevation = LeafDiseaseTheme.dimensions.dimension6),
            text = stringResource(R.string.app_name),
            image = R.drawable.ic_foreground_launcher,
            onIconClicked = {}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LeafDiseaseTheme.colors.background)
                .padding(horizontal = LeafDiseaseTheme.dimensions.dimension32)
                .padding(top = LeafDiseaseTheme.dimensions.dimension24),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = LeafDiseaseTheme.dimensions.dimension12),
                text = stringResource(R.string.home_screen_header),
                style = LeafDiseaseTheme.customTypography.header,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.home_screen_header),
                style = LeafDiseaseTheme.customTypography.subtitle,
                textAlign = TextAlign.Center
            )

            HomeImage(
                modifier = Modifier.padding(vertical = LeafDiseaseTheme.dimensions.dimension40)
            )

            // Selector de modelo experimental
            ModelSelector(
                selectedModel   = selectedModel,
                onModelSelected = onModelSelected,
                modifier        = Modifier.padding(bottom = LeafDiseaseTheme.dimensions.dimension18),
            )

            CoreButton(
                modifier = Modifier.padding(bottom = LeafDiseaseTheme.dimensions.dimension18),
                buttonText = stringResource(R.string.home_screen_take_photo_button),
                buttonImage = R.drawable.ic_round_camera_alt_24,
                buttonType = CoreButtonType.PRIMARY,
                onClick = onTakePhoto
            )

            CoreButton(
                buttonText = stringResource(R.string.home_screen_upload_image_button),
                buttonImage = R.drawable.ic_baseline_image_24,
                buttonType = CoreButtonType.SECONDARY,
                onClick = onUploadImage
            )
        }
    }
}

/**
 * Selector de modelo experimental. Muestra un chip por cada [ModelType] disponible.
 * El chip del modelo activo aparece resaltado en el color primario de la app.
 *
 * Uso experimental — no aparece en la versión de producción (solo en el build de investigación).
 */
@Composable
fun ModelSelector(
    selectedModel: ModelType,
    onModelSelected: (ModelType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Modelo activo",
            style = LeafDiseaseTheme.typography.labelSmall,
            color = LeafDiseaseTheme.colors.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModelType.entries.forEach { type ->
                FilterChip(
                    selected = selectedModel == type,
                    onClick  = { onModelSelected(type) },
                    label    = {
                        Text(
                            text  = type.displayName,
                            style = LeafDiseaseTheme.typography.labelSmall,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor    = LeafDiseaseTheme.colors.primary,
                        selectedLabelColor        = LeafDiseaseTheme.colors.onPrimary,
                    ),
                )
            }
        }
    }
}

@Composable
fun HomeImage(
    modifier: Modifier
) {
    Card(modifier = modifier
        .width(LeafDiseaseTheme.dimensions.dimension180)
        .height(LeafDiseaseTheme.dimensions.dimension180)
        .border(
            width = LeafDiseaseTheme.dimensions.dimension4,
            color = LeafDiseaseTheme.colors.onTertiaryContainer,
            shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension24)
        )
        .clip(RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension24))
        .shadow(
            elevation = LeafDiseaseTheme.dimensions.dimension24
        )
    ) {
        Box(
            modifier = Modifier
            .fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.plant_image_soy),
                contentDescription = "Plant Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = LeafDiseaseTheme.dimensions.dimension24)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.Companion.size(LeafDiseaseTheme.dimensions.dimension32),
                    painter = painterResource(R.drawable.ic_center_focus_strong_24),
                    tint = LeafDiseaseTheme.colors.background,
                    contentDescription = ""
                )

                Text(
                    text = stringResource(R.string.home_screen_image_content).uppercase(),
                    style = LeafDiseaseTheme.customTypography.cardTitle,
                    textAlign = TextAlign.Center,
                    color = LeafDiseaseTheme.colors.background
                )

            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    LeafDiseaseTheme {
        HomeScreen()
    }
}