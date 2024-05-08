package com.rm.view.template

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.rm.R

@Composable
fun FabIcon(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onFabClick() },
        containerColor = colorResource(id = R.color.torinit),
        contentColor = colorResource(id = R.color.white)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add_white_24),
            contentDescription = "Add Resource"
        )
    }
}
