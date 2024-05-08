package com.rm.view.template

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp

@Composable
fun CircularImage(
    image: Int,
    imageDescription: Int,
    imageSize: Dp,
    elevation: Dp
) {
    Card(
        modifier = Modifier
            .size(imageSize),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = stringResource(id = imageDescription),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}