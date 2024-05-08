package com.rm.view.resource

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.masterdata.toResourceProjectType
import com.rm.data.project.ProjectNameList
import com.rm.data.resource.Occupancy
import com.rm.data.resource.ResourceItemList
import com.rm.data.resource.ResourceProjectType
import com.rm.navigation.Route
import com.rm.view.resource.viewmodel.ResourceViewModel
import com.rm.view.template.ChipGroup
import com.rm.view.template.CircularIndicatorView
import com.rm.view.template.FabIcon
import com.rm.view.template.MultiSelectionChip
import com.rm.view.template.OutlinedButtonWithIcon
import com.rm.view.template.SpaceTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentResourceScreen(
    navigateToResourceDetails: (route: String, argument: ResourceItemList) -> Unit,
    navigateToAddResource: (route: String) -> Unit,
    viewModel: ResourceViewModel = hiltViewModel(),
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity)
) {
    val context = LocalContext.current
    val masterData = mainViewModel.masterData
    val projectName = mainViewModel.projectName

    val rList = remember { mutableStateOf(listOf<ResourceItemList>()) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true, block = {
        masterData?.let { viewModel.getResources(mainViewModel, it) }
    })

    rList.value = viewModel.resources

    masterData?.let { mData ->
        projectName?.let { projectName ->
            if (mainViewModel.isLoaderStopped()) {
                InitView(
                    context = context,
                    bottomSheetState = bottomSheetState,
                    scope = scope,
                    masterData = mData,
                    resourceList = rList,
                    projectName = projectName,
                    viewModel = viewModel,
                    navigateToResourceDetails = navigateToResourceDetails,
                    navigateToAddResource = navigateToAddResource
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitView(
    context: Context,
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    resourceList: MutableState<List<ResourceItemList>>,
    projectName: ProjectNameList,
    viewModel: ResourceViewModel,
    navigateToResourceDetails: (route: String, argument: ResourceItemList) -> Unit,
    navigateToAddResource: (route: String) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FabIcon(
                onFabClick = {
                    navigateToAddResource.invoke(Route.HomeNav.AddResource.route)
                }
            )
        }
    ) {
        if (resourceList.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.no_resource_available))
            }
        } else {
            Column {
                Row(
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    TextButton(onClick = {
                        scope.launch { bottomSheetState.show() }
                    }) {
                        Text(
                            text = stringResource(id = R.string.label_filter),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    TextButton(onClick = {
                        Toast.makeText(context, "Sort Clicked", Toast.LENGTH_LONG).show()
                    }) {
                        Text(
                            text = stringResource(id = R.string.label_sort),
                            modifier = Modifier.padding(start = 12.dp),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    TextButton(onClick = {
                        viewModel.clearFilter()
                        Toast.makeText(context, "Filter Cleared", Toast.LENGTH_LONG).show()
                    }) {
                        Text(
                            text = stringResource(id = R.string.label_clear_all),
                            modifier = Modifier.padding(start = 12.dp),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(all = 20.dp)
                ) {
                    items(resourceList.value) { resourceItem ->
                        ResourceCard(resource = resourceItem) { resource ->
                            navigateToResourceDetails.invoke(
                                Route.HomeNav.ResourceDetails.route,
                                resource
                            )
                        }
                    }
                }
            }
        }
    }

    if (bottomSheetState.isVisible) {
        FilterBottomSheetView(
            bottomSheetState,
            scope,
            masterData,
            projectName,
            viewModel
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourceCard(resource: ResourceItemList, onClick: (resource: ResourceItemList) -> Unit) {
    Card(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .clickable { onClick(resource) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // 2.dp,
        // backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White) // CardColors(containerColor = Color.White)

    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .background(colorResource(id = resource.nameProfileBackgroundColor))
                        .padding(8.dp)
                ) {
                    Card(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // 2.dp,2.dp
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.torinit_logo),
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = resource.name,
                        style = TextStyle(
                            color = colorResource(id = R.color.gray_9),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Text(
                    text = resource.designation,
                    style = TextStyle(
                        color = colorResource(id = R.color.gray_9),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .background(colorResource(id = resource.designationBackgroundColor))
                        .padding(8.dp)
                )
            }

            // Showing occupancy of the resource
            val newProjectList = mutableListOf<Occupancy>()
            newProjectList.addAll(resource.projectList)

            var additionalProject = Occupancy()
            resource.occupancy?.let {
                additionalProject = Occupancy(
                    projectName = resource.occupancy.projectName.ifEmpty { "Occupancy" },
                    occupancy = resource.occupancy.occupancy,
                    occupancyHours = resource.occupancy.occupancyHours,
                    occupancyColor = resource.occupancy.occupancyColor,
                    occupancyBackgroundColor = resource.occupancy.occupancyBackgroundColor
                )
            }
            newProjectList.add(additionalProject)

            SpaceTop(top = 20.dp)
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            ) {
                Row(
                    Modifier.weight(.47f).fillMaxHeight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chips to show technologies used by the resource
                    ChipGroup(
                        list = resource.technology
                    )
                }
                Row(
                    Modifier.weight(.06f).fillMaxHeight(.8f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(1f)
                            .align(Alignment.CenterVertically),
                        color = Color.LightGray
                    )
                }
                Row(
                    Modifier.weight(.47f).fillMaxHeight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if ((resource.occupancy == null) || (resource.occupancy.occupancy == 0.0f)) {
                            NoOccupancyCard()
                        } else {
                            FlowRow(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Top
                            ) {
                                newProjectList.forEach {
                                    CircularIndicatorView(
                                        progress = it.occupancy,
                                        text = it.projectName,
                                        indicatorText = it.occupancyHours,
                                        indicatorColor = it.occupancyColor,
                                        indicatorBackgroundColor = it.occupancyBackgroundColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoOccupancyCard() {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "No Project Assigned",
            style = TextStyle(
                color = colorResource(id = R.color.gray_9),
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun OccupancyCard(resourceOccupancy: Occupancy) {
    val animatedProgress = animateFloatAsState(
        targetValue = resourceOccupancy.occupancy,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Occupancy",//resourceOccupancy.projectName,
            style = TextStyle(
                color = colorResource(id = R.color.gray_9),
                fontSize = 14.sp
            )
        )
        LinearProgressIndicator(
            modifier = Modifier
                .width(200.dp)
                .height(10.dp)
                .shadow(shape = CircleShape, elevation = 2.dp)
                .background(color = colorResource(id = resourceOccupancy.occupancyBackgroundColor)),
            color = colorResource(id = resourceOccupancy.occupancyColor),
            progress = animatedProgress
        )
        Text(
            text = resourceOccupancy.occupancyHours,
            style = TextStyle(
                color = colorResource(id = R.color.gray_9),
                fontSize = 14.sp
            )
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun FilterBottomSheetView(
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    projectNameList: ProjectNameList,
    viewModel: ResourceViewModel
) {
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { bottomSheetState.hide() }
        },
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButtonWithIcon(
                    icon = Icons.Rounded.Close,
                    iconDescription = R.string.icon_description_close,
                    buttonText = R.string.label_cancel,
                    buttonOnClick = { scope.launch { bottomSheetState.hide() } }
                )

                OutlinedButtonWithIcon(
                    icon = Icons.Rounded.Done,
                    iconDescription = R.string.icon_description_done,
                    buttonText = R.string.label_done,
                    buttonOnClick = {
                        viewModel.filterResource()
                        scope.launch { bottomSheetState.hide() }
                    }
                )
            }

            Divider(
                thickness = 0.5.dp,
                color = colorResource(id = R.color.gray_9),
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
            )

            MultiSelectionChip(
                columnModifier = Modifier.padding(8.dp),
                textModifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                chipModifier = Modifier.padding(start = 12.dp),
                title = R.string.title_project,
                textSize = 18.sp,
                list = projectNameList.projectNameList,
                selectedList = viewModel.selectedProjectList,
                onSelectionChanged = {
                    val oldList: MutableList<ResourceProjectType> =
                        viewModel.selectedProjectList.toMutableList()
                    val techType = it

                    if (oldList.contains(techType)) {
                        oldList.remove(techType)
                    } else {
                        oldList.add(techType)
                    }

                    viewModel.selectedProjectList.clear()
                    viewModel.selectedProjectList.addAll(oldList)
                }
            )

            SpaceTop(top = 16.dp)
            MultiSelectionChip(
                columnModifier = Modifier.padding(8.dp),
                textModifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                chipModifier = Modifier.padding(start = 12.dp),
                title = R.string.title_designation,
                textSize = 18.sp,
                list = masterData.designation_types.toResourceProjectType(),
                selectedList = viewModel.selectedDesignationList,
                onSelectionChanged = {
                    val oldList: MutableList<ResourceProjectType> =
                        viewModel.selectedDesignationList.toMutableList()
                    val techType = it

                    if (oldList.contains(techType)) {
                        oldList.remove(techType)
                    } else {
                        oldList.add(techType)
                    }
                    viewModel.selectedDesignationList.clear()
                    viewModel.selectedDesignationList.addAll(oldList)
                }
            )

            SpaceTop(top = 16.dp)
            MultiSelectionChip(
                columnModifier = Modifier.padding(8.dp),
                textModifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                chipModifier = Modifier.padding(start = 12.dp),
                title = R.string.title_technologies,
                textSize = 18.sp,
                list = masterData.technologies.toResourceProjectType(),
                selectedList = viewModel.selectedTechnologyList,
                onSelectionChanged = {
                    val oldList: MutableList<ResourceProjectType> =
                        viewModel.selectedTechnologyList.toMutableList()
                    val techType = it

                    if (oldList.contains(techType)) {
                        oldList.remove(techType)
                    } else {
                        oldList.add(techType)
                    }

                    viewModel.selectedTechnologyList.clear()
                    viewModel.selectedTechnologyList.addAll(oldList)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComponentResourceScreen({ _, _ -> }, {})
}
