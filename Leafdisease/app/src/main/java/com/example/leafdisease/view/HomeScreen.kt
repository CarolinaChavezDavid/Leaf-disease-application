package com.example.leafdisease.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.leafdisease.R
import com.example.leafdisease.ui.theme.LeafDiseaseTheme


@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LeafDiseaseTheme.colors.background)
            .padding(horizontal = LeafDiseaseTheme.dimensions.dimension24)
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
        Image(
            painter = painterResource(R.drawable.plant_image),
            contentDescription = "Plant Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }

}


@Composable
@Preview
fun HomeScreenPreview() {
    LeafDiseaseTheme {
        HomeScreen()
    }
}