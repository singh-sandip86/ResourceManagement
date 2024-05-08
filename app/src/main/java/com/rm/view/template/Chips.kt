package com.rm.view.template

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    list: List<String>
) {
    Column(modifier = Modifier.padding(8.dp)) {
        FlowRow() {
            list.forEach {
                Chip(name = it)
            }
        }
    }
}

@Composable
fun Chip(name: String = "Chip") {
    Surface(
        modifier = Modifier.padding(4.dp),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(modifier = Modifier) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
