package com.rm.view.template

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R
import com.rm.data.resource.ResourceProjectType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiSelectionChip(
    columnModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    chipModifier: Modifier = Modifier,
    title: Int,
    textSize: TextUnit = 16.sp,
    list: List<ResourceProjectType>,
    selectedList: List<ResourceProjectType> = listOf(),
    onSelectionChanged: (ResourceProjectType) -> Unit = {}
) {
    Column(modifier = columnModifier) {
        Text(
            text = stringResource(id = title),
            style = TextStyle(
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.gray_9),
                letterSpacing = 2.sp
            ),
            modifier = textModifier
        )

        FlowRow(modifier = chipModifier) {
            list.forEach {
                ToggleableChip(
                    chip = it,
                    isSelected = selectedList.contains(it),
                    onSelectionChanged = {
                        onSelectionChanged(it)
                    },
                )
            }
        }
    }
}

@Composable
fun ToggleableChip(
    chip: ResourceProjectType,
    isSelected: Boolean = false,
    onSelectionChanged: (ResourceProjectType) -> Unit = {}
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.large,
        color = if (isSelected) colorResource(id = com.rm.R.color.torinit) else MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = isSelected,
                    onValueChange = { onSelectionChanged(chip) }
                )
        ) {
            Text(
                text = chip.value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}