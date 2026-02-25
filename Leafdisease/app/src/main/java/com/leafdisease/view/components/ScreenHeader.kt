package com.leafdisease.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.leafdisease.R
import com.leafdisease.ui.theme.LeafDiseaseTheme

@Composable
fun ScreenHeader(
    text: String,
    icon: Int? = null,
    image: Int? = null,
    modifier: Modifier = Modifier,
    onIconClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(LeafDiseaseTheme.dimensions.dimension52)
            .shadow(elevation = LeafDiseaseTheme.dimensions.dimension6)
            .background(LeafDiseaseTheme.colors.surface)
            .padding(horizontal = LeafDiseaseTheme.dimensions.dimension16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        image?.let {
            Image(
                modifier = Modifier
                    .clickable{
                        onIconClicked.invoke()
                    }.clip(CircleShape)
                    .size(LeafDiseaseTheme.dimensions.dimension40),
                painter = painterResource(it),
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(LeafDiseaseTheme.dimensions.dimension8))

        }

        icon?.let {
            IconButton(onClick = onIconClicked) {
                Icon(
                    painter = painterResource(it),
                    modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension24),
                    contentDescription = "",
                    tint = LeafDiseaseTheme.colors.onPrimary
                )

            }

            Spacer(modifier = Modifier.width(LeafDiseaseTheme.dimensions.dimension16))
        }




        Text(
            text = text,
            style = LeafDiseaseTheme.customTypography.headerSmall
        )
    }
}

@Preview
@Composable
fun ScreenHeaderPreview() {
    LeafDiseaseTheme {
        ScreenHeader(
            text = "Leaf Disease Detection",
            image = R.drawable.ic_round_arrow_left_24,
            onIconClicked = {}
        )
    }
}