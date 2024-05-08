package com.rm.view.template

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R
import com.rm.api.response.masterdata.Type
import com.rm.utils.setMutableStateValue
import com.rm.view.common.UIDropdownField
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitledExposedDropdownMenu(
    title: Int,
    items: List<Type>,
    uiDropdownField: UIDropdownField? = null,
    selected: Type = items[0],
    onItemSelected: (Type) -> Unit,
) {
    var selectedItem by remember { mutableStateOf(selected) }
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    if (uiDropdownField != null) {
        setMutableStateValue(state = uiDropdownField.state, value = selectedItem.value)
    }

    Text(
        text = stringResource(id = title),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.gray_9),
            letterSpacing = 2.sp
        )
    )

    SpaceTop(top = 12.dp)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions
            .filter { it is PressInteraction.Press }
            .collect {
                expanded = !expanded
            }
    }

    ExposedDropdownMenuStack(
        textField = {
            OutlinedTextField(
                value = selectedItem.value,
                onValueChange = { if (uiDropdownField != null) {
                        setMutableStateValue(state = uiDropdownField.state, value = it)
                    }
                },
                interactionSource = interactionSource,
                readOnly = true,
                trailingIcon = {
                    val rotation by animateFloatAsState(if (expanded) 180F else 0F)
                    Icon(
                        rememberVectorPainter(Icons.Default.ArrowDropDown),
                        contentDescription = stringResource(id = R.string.content_desc_dropdown_arrow),
                        Modifier.rotate(rotation),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = colorResource(id = R.color.gray_9),
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.black),
                    unfocusedBorderColor = colorResource(id = R.color.black)
                )
            )
        },
        dropdownMenu = { boxWidth, itemHeight ->
            Box(
                Modifier
                    .width(boxWidth)
                    .wrapContentSize(Alignment.TopStart)
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .height(itemHeight)
                                .width(boxWidth),
                            onClick = {
                                expanded = false
                                selectedItem = items[index]
                                if (uiDropdownField != null) {
                                    setMutableStateValue(
                                        state = uiDropdownField.state,
                                        value = selectedItem.value
                                    )
                                }
                                onItemSelected(item)
                            },
                            text = {
                                Text(
                                    text = item.value,
                                    style = TextStyle(
                                        color = colorResource(id = R.color.gray_9),
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    )

    SpaceTop(top = 20.dp)
}

@Composable
private fun ExposedDropdownMenuStack(
    textField: @Composable () -> Unit,
    dropdownMenu: @Composable (boxWidth: Dp, itemHeight: Dp) -> Unit
) {
    SubcomposeLayout { constraints ->
        val textFieldPlaceable =
            subcompose(ExposedDropdownMenuSlot.TextField, textField).first().measure(constraints)
        val dropdownPlaceable = subcompose(ExposedDropdownMenuSlot.Dropdown) {
            dropdownMenu(textFieldPlaceable.width.toDp(), textFieldPlaceable.height.toDp())
        }.first().measure(constraints)
        layout(textFieldPlaceable.width, textFieldPlaceable.height) {
            textFieldPlaceable.placeRelative(0, 0)
            dropdownPlaceable.placeRelative(0, textFieldPlaceable.height)
        }
    }
}

private enum class ExposedDropdownMenuSlot { TextField, Dropdown }