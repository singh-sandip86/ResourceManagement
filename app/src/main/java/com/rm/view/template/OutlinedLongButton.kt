package com.rm.view.template

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R

@Composable
fun OutlinedLongButton(
    buttonText: Int,
    onButtonClick: () -> Unit
) {
    Button(
        onClick = { onButtonClick() },
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.white),
            contentColor = colorResource(id = R.color.torinit)
        ),
        border = BorderStroke(width = 1.dp, color = colorResource(id = R.color.torinit))
    ) {
        Text(
            text = stringResource(id = buttonText),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )
    }
}