package com.example.leafdisease.view.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.leafdisease.ui.theme.LeafDiseaseTheme

@Composable
fun CoreButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
    ) {
        Button(
            onClick = { onClick.invoke() },
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(LeafDiseaseTheme.dimensions.dimension32),
            contentPadding =   PaddingValues(
                start =  LeafDiseaseTheme.dimensions.dimension12,
                top =  LeafDiseaseTheme.dimensions.dimension20,
                end =  LeafDiseaseTheme.dimensions.dimension12,
                bottom =  LeafDiseaseTheme.dimensions.dimension20,
            )
        ) {
            Text(
                text = buttonText,
                style = LeafDiseaseTheme.typography.labelMedium
            )
        }
    }
}