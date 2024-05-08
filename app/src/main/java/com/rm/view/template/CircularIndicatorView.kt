package com.rm.view.template

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R

@Composable
fun CircularIndicatorView(
    progress: Float,
    text: String,
    indicatorText: String,
    indicatorColor: Int,
    indicatorBackgroundColor: Int
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    Column(
        modifier = Modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .shadow(shape = CircleShape, elevation = 2.dp)
                    .background(color = colorResource(id = indicatorBackgroundColor)),
                color = colorResource(id = indicatorColor),
                progress = animatedProgress
            )

            Text(
                text = indicatorText,
                style = TextStyle(
                    color = colorResource(id = R.color.gray_9),
                    fontSize = 14.sp
                )
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = text,
            style = TextStyle(
                color = colorResource(id = R.color.gray_9),
                fontSize = 14.sp
            )
        )
    }
}