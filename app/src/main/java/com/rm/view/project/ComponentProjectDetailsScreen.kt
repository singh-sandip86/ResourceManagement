@file:OptIn(ExperimentalMaterial3Api::class)

package com.rm.view.project

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.data.project.ProjectDetails
import com.rm.data.project.ProjectResources
import com.rm.data.project.Resource
import com.rm.data.resource.ResourceItemList
import com.rm.navigation.Route
import com.rm.utils.empty
import com.rm.utils.millisToString
import com.rm.view.project.bottomsheet.AddMemberBottomSheetView
import com.rm.view.project.viewmodel.ProjectDetailsViewModel
import com.rm.view.resource.viewmodel.ResourceViewModel
import com.rm.view.template.BottomView
import com.rm.view.template.CircularBorderedImage
import com.rm.view.template.CircularImage
import com.rm.view.template.Space
import com.rm.view.template.SpaceTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ComponentProjectDetailsScreen(
    projectId: String?,
    viewModel: ProjectDetailsViewModel = hiltViewModel(),
    resourceViewModel: ResourceViewModel = hiltViewModel(),
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity),
    navigateToAddProject: (route: String, editEnable: Boolean, project: ProjectDetailsResponseData) -> Unit,
    onBackPress: (route: String) -> Unit
) {
    val context = LocalContext.current
    val masterData = mainViewModel.masterData

    val scope = rememberCoroutineScope()
    val addMemberBSS = rememberModalBottomSheetState()

    val rList = remember { mutableStateOf(listOf<ResourceItemList>()) }

    projectId?.let { id ->
        viewModel.selectedProjectId = id
    }

    LaunchedEffect(key1 = true, block = {
        masterData?.let { masterData ->
            viewModel.getProjectDetail(mainViewModel = mainViewModel, masterData = masterData)
            resourceViewModel.getResources(mainViewModel = mainViewModel, masterData = masterData)
        }
    })

    rList.value = resourceViewModel.resources

    LaunchedEffect(key1 = true, block = {
        viewModel.projectDeleted.collect {
            if (it) {
                Toast.makeText(context, "Project Deleted Successfully", Toast.LENGTH_LONG).show()
                onBackPress.invoke(Route.HomeNav.Project.route)
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        viewModel.projectUpdated.collect {
            if (it) {
                Toast.makeText(context, "Project Updated Successfully", Toast.LENGTH_LONG).show()
                onBackPress.invoke(Route.HomeNav.Project.route)
            }
        }
    })

    val projectDetails = remember { viewModel.projectDetail }

    Scaffold(
        topBar = {
            TopBar("Project Details") {
                onBackPress.invoke(Route.HomeNav.Project.route)
            }
        }
    ) { paddingValues ->
        if (mainViewModel.isLoaderStopped()) {
            masterData?.let { data ->
                InitView(
                    paddingValues,
                    context,
                    scope,
                    addMemberBSS,
                    masterData = data,
                    resourceList = rList,
                    projectDetails.value,
                    viewModel,
                    mainViewModel,
                    navigateToAddProject,
                    onBackPress
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(title: String, onClick: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(id = R.color.torinit),
            titleContentColor = colorResource(id = R.color.white)
        ),
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(
                onClick = { onClick.invoke() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colorResource(
                        id = R.color.torinit
                    ),
                    contentColor = colorResource(
                        id = R.color.white
                    )
                )
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun InitView(
    paddingValues: PaddingValues,
    context: Context,
    scope: CoroutineScope,
    addMemberBSS: SheetState,
    masterData: MasterDataResponseData,
    resourceList: MutableState<List<ResourceItemList>>,
    project: ProjectDetails,
    viewModel: ProjectDetailsViewModel,
    mainViewModel: MainActivityViewModel,
    navigateToAddProject: (route: String, editEnable: Boolean, project: ProjectDetailsResponseData) -> Unit,
    onBackPress: (route: String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.White)
            ) {
                InitProjectDetailView(
                    context,
                    scope,
                    addMemberBSS,
                    project,
                    viewModel
                )
            }

            // Bottom View
            BottomView(
                leftButtonText = if (viewModel.resourceEdited.value) stringResource(id = R.string.label_cancel) else stringResource(
                    id = R.string.label_delete
                ),
                rightButtonText = if (viewModel.resourceEdited.value) stringResource(id = R.string.label_submit) else stringResource(
                    id = R.string.label_edit
                ),
                onLeftButtonClick = {
                    if (viewModel.resourceEdited.value) {
                        // Cancel Click
                        onBackPress.invoke(Route.HomeNav.Project.route)
                    } else {
                        // Delete Click
                        viewModel.deleteProject(mainViewModel)
                    }
                },
                onRightButtonClick = {
                    if (viewModel.resourceEdited.value) {
                        // Submit Click
                        viewModel.updateProject(
                            mainViewModel = mainViewModel,
                            masterData = masterData
                        )
                        viewModel.resourceEdited.value = false
                    } else {
                        // Edit Click
                        navigateToAddProject.invoke(
                            Route.HomeNav.AddProject.route,
                            true,
                            viewModel._projectDetailsResponse.value
                        )
                    }
                }
            )
        }

        // Add Member Bottom Sheet
        if (addMemberBSS.isVisible) {
            val selectedMemberList: MutableList<Resource> = mutableListOf()
            val selectedList = if (viewModel.isFromSubProject) {
                selectedMemberList.addAll(project.subProjectList[viewModel.subProjectIndex].subProjectResourceList[viewModel.selectedIndex].resourceList)
                project.subProjectList[viewModel.subProjectIndex].subProjectResourceList[viewModel.selectedIndex].resourceList.map { it.resourceId }
            } else {
                selectedMemberList.addAll(project.projectResourceList[viewModel.selectedIndex].resourceList)
                project.projectResourceList[viewModel.selectedIndex].resourceList.map { it.resourceId }
            }

            AddMemberBottomSheetView(
                addMemberBottomSheetState = addMemberBSS,
                scope = scope,
                memberList = resourceList.value,
                selectedMemberList = selectedList,
                onDoneClick = { memberList ->
                    viewModel.resourceEdited.value = true
                    selectedMemberList.clear()

                    val selectedResourceType = if (viewModel.isFromSubProject) {
                        project.subProjectList[viewModel.subProjectIndex].subProjectResourceList[viewModel.selectedIndex].resourceType
                    } else {
                        project.projectResourceList[viewModel.selectedIndex].resourceType
                    }

                    memberList.forEach {
                        selectedMemberList.add(
                            Resource(
                                resourceId = it.id,
                                resourceName = it.name,
                                resourceImage = String.empty(),
                                designation = it.designation,
                                allocatedDesignation = selectedResourceType,
                                allocatedHours = "4 hr",
                                hoursProgressIndicator = 0.5f,
                                hoursProgressIndicatorColor = R.color.green_6,
                                hoursProgressIndicatorBackgroundColor = R.color.green_1,
                                allocatedFrom = 1644451200000,
                                allocatedTill = 1702425600000
                            )
                        )
                    }

                    // Add Back to original List
                    if (viewModel.isFromSubProject) {
                        project.subProjectList[viewModel.subProjectIndex].subProjectResourceList[viewModel.selectedIndex].resourceList =
                            selectedMemberList
                        project.subProjectList[viewModel.subProjectIndex].subProjectResourceList[viewModel.selectedIndex].allocationCount++
                    } else {
                        project.projectResourceList[viewModel.selectedIndex].resourceList =
                            selectedMemberList
                        project.projectResourceList[viewModel.selectedIndex].allocationCount++
                    }
                }
            )
        }
    }
}

@Composable
fun InitProjectDetailView(
    context: Context,
    scope: CoroutineScope,
    addMemberBSS: SheetState,
    project: ProjectDetails,
    viewModel: ProjectDetailsViewModel,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                painter = painterResource(id = R.drawable.client_havas1),
                contentDescription = stringResource(id = R.string.client_logo_description),
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CircularBorderedImage(
                    image = R.drawable.project_scene,
                    imageDescription = R.string.project_logo_description,
                    imageSize = 80.dp,
                    elevation = 2.dp
                )
            }
        }

        // Project Scope
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            project.projectType.forEachIndexed { index, tech ->
                Text(
                    text = tech,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.gray_9),
                        fontWeight = FontWeight.Bold
                    )
                )
                if ((project.projectType.size - 1 > index)) {
                    Text(
                        text = "    •    ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.gray_9),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        DescriptionAndDate(
            description = stringResource(id = R.string.project_description),
            startDate = project.projectStartDate.millisToString(),
            endDate = project.projectEndDate.millisToString()
        )

        // Project Resource
        if (project.projectResourceList.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                text = stringResource(id = R.string.project_resources),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black),
                    letterSpacing = 0.75.sp
                )
            )
        }

        project.projectResourceList.forEachIndexed { index, projectResources ->
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.gray_6),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
            {
                ResourceHeader(
                    title = projectResources.resourceType,
                    count = "${projectResources.allocationCount}/${projectResources.totalCount}"
                )
                projectResources.resourceList.forEach { resource ->
                    ProjectResourceCard(resource = resource, onDeleteClick = {
                        viewModel.resourceEdited.value = true
                        val oldList = mutableListOf<Resource>()
                        oldList.addAll(projectResources.resourceList)
                        oldList.remove(it)
                        projectResources.resourceList = oldList
                        Toast.makeText(context, "Resource Removed", Toast.LENGTH_LONG).show()
                    })
                }
                AddButton(
                    scope = scope,
                    addMemberBSS = addMemberBSS
                ) {
                    viewModel.isFromSubProject = false
                    viewModel.selectedIndex = index
                }
                SpaceTop(top = 20.dp)
            }
            SpaceTop(top = 20.dp)
        }

        //Sub-Project List
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            text = stringResource(id = R.string.project_subprojects),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black),
                letterSpacing = 0.75.sp
            )
        )

        project.subProjectList.forEachIndexed { index, subProjects ->
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.gray_6),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    text = subProjects.subProjectName,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black),
                        letterSpacing = 0.75.sp
                    )
                )
                DescriptionAndDate(
                    description = stringResource(id = R.string.project_description),
                    startDate = subProjects.subProjectStartDate.millisToString(),
                    endDate = subProjects.subProjectEndDate.millisToString()
                )
                SubProjectResources(
                    context = context,
                    scope = scope,
                    addMemberBSS = addMemberBSS,
                    resourceList = subProjects.subProjectResourceList,
                    subProjectIndex = index,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ResourceHeader(title: String, count: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black),
                letterSpacing = 0.75.sp
            )
        )
        Text(
            text = count,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.black),
                letterSpacing = 0.75.sp
            )
        )
    }
}

@Composable
fun DescriptionAndDate(
    description: String,
    startDate: String,
    endDate: String
) {
    // Project Description
    Text(
        modifier = Modifier.padding(20.dp),
        text = description,
        style = TextStyle(
            fontSize = 16.sp,
            color = colorResource(id = R.color.gray_9),
            letterSpacing = 0.75.sp
        )
    )

    // Start & End Date
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.project_start_date),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black),
                    letterSpacing = 0.75.sp
                )
            )
            Text(
                text = startDate,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 0.75.sp
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.project_end_date),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black),
                    letterSpacing = 0.75.sp
                )
            )
            Text(
                text = endDate,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 0.75.sp
                )
            )
        }
    }
}

@Composable
fun ProjectResourceCard(
    resource: Resource,
    onDeleteClick: (resource: Resource) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        text = resource.resourceName,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.gray_9),
                            letterSpacing = 0.75.sp
                        )
                    )
                    Text(
                        text = resource.designation,
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
                            .background(color = colorResource(id = resource.hoursProgressIndicatorBackgroundColor)),
                        color = colorResource(id = resource.hoursProgressIndicatorColor),
                        progress = resource.hoursProgressIndicator
                    )

                    Text(
                        text = resource.allocatedHours,
                        style = TextStyle(
                            color = colorResource(id = R.color.gray_9),
                            fontSize = 13.sp
                        )
                    )
                }

                IconButton(onClick = { onDeleteClick(resource) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete_20),
                        contentDescription = "Delete Resource",
                        tint = colorResource(id = R.color.red_6)
                    )
                }
            }
        }
    }
}

@Composable
fun SubProjectResources(
    context: Context,
    scope: CoroutineScope,
    addMemberBSS: SheetState,
    resourceList: List<ProjectResources>,
    subProjectIndex: Int,
    viewModel: ProjectDetailsViewModel
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        text = stringResource(id = R.string.project_resources),
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.black),
            letterSpacing = 0.75.sp
        )
    )

    resourceList.forEachIndexed { index, resource ->
        Column(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.gray_6),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        {
            ResourceHeader(
                title = resource.resourceType,
                count = "${resource.allocationCount}/${resource.totalCount}"
            )
            resource.resourceList.forEach {
                ProjectResourceCard(resource = it, onDeleteClick = {
                    viewModel.resourceEdited.value = true
                    val oldList = mutableListOf<Resource>()
                    oldList.addAll(resource.resourceList)
                    oldList.remove(it)
                    resource.resourceList = oldList
                    Toast.makeText(context, "Resource Removed", Toast.LENGTH_LONG).show()
                })
            }
            AddButton(
                scope = scope,
                addMemberBSS = addMemberBSS
            ) {
                viewModel.isFromSubProject = true
                viewModel.subProjectIndex = subProjectIndex
                viewModel.selectedIndex = index
            }
            SpaceTop(top = 20.dp)
        }
    }
}

@Composable
fun AddButton(
    scope: CoroutineScope,
    addMemberBSS: SheetState,
    onAddButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedIconButton(
            onClick = {
                onAddButtonClick()
                scope.launch { addMemberBSS.show() }
            },
            border = BorderStroke(1.dp, colorResource(id = R.color.torinit)),
            modifier = Modifier.width(200.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_white_24),
                    contentDescription = "Add"
                )
                Space(start = 8.dp)
                Text(
                    text = stringResource(id = R.string.label_add),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.torinit),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}