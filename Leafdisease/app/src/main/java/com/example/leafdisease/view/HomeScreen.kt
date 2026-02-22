package com.example.leafdisease.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import com.example.leafdisease.R
import com.example.leafdisease.ui.theme.LeafDiseaseTheme
import com.example.leafdisease.view.components.CoreButton


@Composable
fun HomeScreen() {
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
            style = LeafDiseaseTheme.typography.headlineLarge
        )

        Text(
            text = stringResource(R.string.home_screen_header),
            style = LeafDiseaseTheme.typography.titleLarge
        )

        HomeImage(
            modifier = Modifier
        )

        CoreButton(
            modifier = Modifier.padding(bottom = LeafDiseaseTheme.dimensions.dimension18),
            buttonText = stringResource(R.string.home_screen_take_photo_button),
            onClick = {}
        )

        CoreButton(
            buttonText = stringResource(R.string.home_screen_upload_image_button),
            onClick = {}
        )
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
            color = LeafDiseaseTheme.colors.onBackground,
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
                    modifier = Modifier.size(LeafDiseaseTheme.dimensions.dimension32),
                    painter = painterResource(R.drawable.ic_center_focus_strong_24),
                    tint = LeafDiseaseTheme.colors.background,
                    contentDescription = ""
                )

                Text(
                    text = stringResource(R.string.home_screen_image_content).uppercase(),
                    style = LeafDiseaseTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
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