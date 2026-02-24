package com.example.leafdisease.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.leafdisease.R
import com.example.leafdisease.ui.theme.LeafDiseaseTheme

@Composable
fun CoreButton(
    buttonText: String,
    buttonImage: Int,
    buttonType: CoreButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick.invoke() },
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = LeafDiseaseTheme.dimensions.dimension6,
                shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension32),
                ambientColor = getStrokeColor(buttonType),
                spotColor = getStrokeColor(buttonType)
            )
            .border(
                LeafDiseaseTheme.dimensions.dimension1,
                getStrokeColor(buttonType),
                RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension32)
            ),
        shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension32),
        contentPadding =   PaddingValues(
            start =  LeafDiseaseTheme.dimensions.dimension12,
            top =  LeafDiseaseTheme.dimensions.dimension20,
            end =  LeafDiseaseTheme.dimensions.dimension12,
            bottom =  LeafDiseaseTheme.dimensions.dimension20,
        ),
        colors = getButtonColors(buttonType)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(buttonImage),
                contentDescription = null,
                modifier = Modifier
                    .height(LeafDiseaseTheme.dimensions.dimension24)
                    .padding(end = LeafDiseaseTheme.dimensions.dimension8),
                tint = getTintColor(buttonType)
            )
            Text(
                text = buttonText,
                style = LeafDiseaseTheme.customTypography.buttonText
            )
        }
    }
    
}

@Composable
fun getStrokeColor(buttonType: CoreButtonType): Color {
    return when (buttonType) {
        CoreButtonType.PRIMARY -> LeafDiseaseTheme.colors.primary
        CoreButtonType.SECONDARY -> LeafDiseaseTheme.colors.onSurface
        CoreButtonType.CANCEL -> LeafDiseaseTheme.colors.primaryContainer
    }
}

@Composable
fun getTintColor(buttonType: CoreButtonType): Color {
    return when (buttonType) {
        CoreButtonType.PRIMARY -> LeafDiseaseTheme.colors.surface
        CoreButtonType.SECONDARY, CoreButtonType.CANCEL -> LeafDiseaseTheme.colors.primary
    }
}

@Composable
fun getButtonColors(buttonType: CoreButtonType): ButtonColors {
    return when (buttonType) {
        CoreButtonType.PRIMARY -> ButtonColors(
            containerColor = LeafDiseaseTheme.colors.primary,
            contentColor = LeafDiseaseTheme.colors.background,
            disabledContainerColor = LeafDiseaseTheme.colors.primary.copy(alpha = 0.38f),
            disabledContentColor = LeafDiseaseTheme.colors.background.copy(alpha = 0.38f)
        )
        CoreButtonType.SECONDARY -> ButtonColors(
            containerColor = LeafDiseaseTheme.colors.surface,
            contentColor = LeafDiseaseTheme.colors.onBackground,
            disabledContainerColor = LeafDiseaseTheme.colors.background.copy(alpha = 0.38f),
            disabledContentColor = LeafDiseaseTheme.colors.onBackground.copy(alpha = 0.38f)
        )
        CoreButtonType.CANCEL -> ButtonColors(
            containerColor = LeafDiseaseTheme.colors.primaryContainer,
            contentColor = LeafDiseaseTheme.colors.primary,
            disabledContainerColor = LeafDiseaseTheme.colors.background.copy(alpha = 0.38f),
            disabledContentColor = LeafDiseaseTheme.colors.onBackground.copy(alpha = 0.38f)
        )
    }
}


enum class CoreButtonType {
    PRIMARY, SECONDARY, CANCEL
}

@Preview
@Composable
fun CoreButtonPreview() {
    LeafDiseaseTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LeafDiseaseTheme.colors.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CoreButton(
                    buttonText = "Take Photo",
            buttonImage = R.drawable.ic_round_camera_alt_24,
            buttonType = CoreButtonType.PRIMARY,
            onClick = {}
            )
            CoreButton(
                buttonText = "Upload Image",
                buttonImage = R.drawable.ic_baseline_image_24,
                buttonType = CoreButtonType.SECONDARY,
                onClick = {}
            )

            CoreButton(
                buttonText = "Cancel analysis",
                buttonImage = R.drawable.ic_close_24,
                buttonType = CoreButtonType.CANCEL,
                onClick = {}
            )
        }
    }
}