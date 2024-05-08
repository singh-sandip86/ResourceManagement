package com.rm.view.project.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R
import com.rm.api.response.masterdata.Type
import com.rm.view.template.BottomView
import com.rm.view.template.SpaceTop
import com.rm.view.template.TitledExposedDropdownMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceBottomSheetView(
    addResourceBottomSheetState: SheetState,
    scope: CoroutineScope,
    designationTypeList: List<Type>,
    selectedDesignation: Type = designationTypeList[0],
    onDoneClick: (type: Type, count: Int) -> Unit,
) {
    val selectedType = remember { mutableStateOf(designationTypeList[0]) }
    val count = remember { mutableStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { addResourceBottomSheetState.hide() }
        },
        sheetState = addResourceBottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.label_resource),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 2.sp
                )
            )
            Divider(
                thickness = 0.5.dp,
                color = colorResource(id = R.color.gray_9),
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                TitledExposedDropdownMenu(
                    title = R.string.label_type,
                    items = designationTypeList,
                    selected = selectedDesignation,
                    onItemSelected = {
                        selectedType.value = it
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.label_count),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.gray_9),
                            letterSpacing = 2.sp
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (count.value > 0) {
                                count.value = count.value - 1
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_remove_24),
                                contentDescription = stringResource(id = R.string.content_desc_remove)
                            )
                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = count.value.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.gray_9),
                            )
                        )
                        IconButton(onClick = { count.value = count.value + 1 }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add_24),
                                contentDescription = stringResource(id = R.string.content_desc_add)
                            )
                        }
                    }
                }

                SpaceTop(top = 20.dp)
            }

            BottomView(
                leftButtonText = stringResource(id = R.string.label_cancel),
                rightButtonText = stringResource(id = R.string.label_done),
                onLeftButtonClick = { scope.launch { addResourceBottomSheetState.hide() } },
                onRightButtonClick = {
                    onDoneClick(selectedType.value, count.value)
                    scope.launch { addResourceBottomSheetState.hide() }
                }
            )
        }
    }
}