package com.rm.view.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R

@Composable
fun OutlinedButtonWithIcon(
    icon: ImageVector,
    iconDescription: Int,
    buttonText: Int,
    buttonOnClick: () -> Unit
) {
    OutlinedIconButton(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(100.dp),
        onClick = { buttonOnClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = iconDescription),
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = stringResource(id = buttonText),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 2.sp
                )
            )
        }
    }
}