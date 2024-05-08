package com.rm.view.project.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R
import com.rm.data.resource.ResourceItemList
import com.rm.view.template.BottomView
import com.rm.view.template.CircularImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberBottomSheetView(
    addMemberBottomSheetState: SheetState,
    scope: CoroutineScope,
    memberList: List<ResourceItemList>,
    selectedMemberList: List<String>, // List of Id's of selected members
    onDoneClick: (memberList: List<ResourceItemList>) -> Unit,
) {
    val selectedList: MutableList<ResourceItemList> = mutableListOf()

    memberList.forEach {
        if (selectedMemberList.contains(it.id)) {
            selectedList.add(it)
        }
    }

    val selectedMList = remember { selectedList }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { addMemberBottomSheetState.hide() }
        },
        sheetState = addMemberBottomSheetState,
        containerColor = colorResource(id = R.color.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.label_member_list),
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(all = 20.dp)
                ) {
                    items(memberList) { memberItem ->
                        MemberCard(
                            member = memberItem,
                            isMemberSelected = selectedMList.contains(memberItem),
                            onClick = { member, isChecked ->
                                if (isChecked && selectedMList.contains(member).not()) {
                                    selectedMList.add(member)
                                } else if (isChecked.not() && selectedMList.contains(member)) {
                                    selectedMList.remove(member)
                                }
                            }
                        )
                    }
                }
            }

            // Bottom View
            BottomView(
                leftButtonText = stringResource(id = R.string.label_cancel),
                rightButtonText = stringResource(id = R.string.label_done),
                onLeftButtonClick = {
                    selectedMList.clear()
                    scope.launch { addMemberBottomSheetState.hide() }
                },
                onRightButtonClick = {
                    onDoneClick(selectedMList)
                    selectedMList.clear()
                    scope.launch { addMemberBottomSheetState.hide() }
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun MemberCard(
    member: ResourceItemList,
    isMemberSelected: Boolean,
    onClick: (resource: ResourceItemList, isChecked: Boolean) -> Unit
) {
    val checkedState = remember { mutableStateOf(isMemberSelected) }

    Card(
        onClick = { /*onClick(member)*/ },
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        enabled = ((member.occupancy.occupancy >= 1.0f) && isMemberSelected.not()).not(),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 4.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularImage(
                    image = R.drawable.torinit_logo,
                    imageDescription = R.string.resource_logo_description,
                    imageSize = 40.dp,
                    elevation = 2.dp
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = member.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.gray_9),
                            letterSpacing = 0.75.sp
                        )
                    )
                    Text(
                        text = member.designation,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.gray_9),
                            letterSpacing = 0.75.sp
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(44.dp)
                            .height(44.dp)
                            .shadow(shape = CircleShape, elevation = 2.dp)
                            .background(color = colorResource(id = member.occupancy.occupancyBackgroundColor)),
                        color = colorResource(id = member.occupancy.occupancyColor),
                        progress = member.occupancy.occupancy
                    )

                    Text(
                        text = member.occupancy.occupancyHours,
                        style = TextStyle(
                            color = colorResource(id = R.color.gray_9),
                            fontSize = 13.sp
                        )
                    )
                }

                Checkbox(
                    checked = checkedState.value,
                    enabled = ((member.occupancy.occupancy >= 1.0f) && isMemberSelected.not()).not(),
                    onCheckedChange = {
                        checkedState.value = it
                        onClick(member, it)
                    }
                )
            }
        }
    }
}