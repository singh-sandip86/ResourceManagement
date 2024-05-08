package com.rm.view.template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.api.response.masterdata.Type

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupedCheckbox(sectionTitle: String, itemList: List<Type>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = sectionTitle,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 20.dp, bottom = 16.dp)
        )

        FlowRow {
            itemList.forEach { items ->
                Row(
                    modifier = Modifier.padding(horizontal =  8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isChecked = remember { mutableStateOf(false) }

                    Checkbox(
                        checked = isChecked.value,
                        onCheckedChange = { isChecked.value = it },
                        enabled = true,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Magenta,
                            uncheckedColor = Color.DarkGray,
                            checkmarkColor = Color.Cyan
                        )
                    )
                    Text(
                        text = items.value,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}