package com.rm.view.template

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R

@Composable
fun BottomView(
    leftButtonText: String,
    rightButtonText: String,
    isLeftButtonEnabled: Boolean = true,
    isRightButtonEnabled: Boolean = true,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            // elevation = 10.dp,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White)
            // backgroundColor = colorResource(id = R.color.white),
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = { onLeftButtonClick() },
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.torinit),
                        contentColor = colorResource(id = R.color.white)
                    ),
                    enabled = isLeftButtonEnabled
                ) {
                    Text(
                        text = leftButtonText,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                }
                Button(
                    onClick = { onRightButtonClick() },
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.torinit),
                        contentColor = colorResource(id = R.color.white)
                    ),
                    enabled = isRightButtonEnabled
                ) {
                    Text(
                        text = rightButtonText,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                }
            }
        }
    }
}