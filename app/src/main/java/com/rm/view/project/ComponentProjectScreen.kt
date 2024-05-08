package com.rm.view.project

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
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
import com.rm.data.project.ProjectListItem
import com.rm.navigation.Route
import com.rm.utils.millisToString
import com.rm.view.project.viewmodel.ProjectViewModel
import com.rm.view.template.CircularIndicatorView
import com.rm.view.template.FabIcon

@Composable
fun ComponentProjectScreen(
    viewModel: ProjectViewModel = hiltViewModel(),
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity),
    navigateToProjectDetails: (route: String, argument: ProjectListItem) -> Unit,
    navigateToAddProject: (route: String) -> Unit,
) {
    val context = LocalContext.current
    val masterData = mainViewModel.masterData

    val pList = remember { mutableStateOf(listOf<ProjectListItem>()) }

    LaunchedEffect(key1 = true, block = {
        masterData?.let { viewModel.getProjects(mainViewModel, it) }
    })

    pList.value = viewModel.projects

    masterData?.let {
        if (mainViewModel.isLoaderStopped()) {
            InitView(
                context = context,
                masterData = it,
                projectList = pList,
                viewModel = viewModel,
                navigateToProjectDetails = navigateToProjectDetails,
                navigateToAddProject = navigateToAddProject
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitView(
    context: Context,
//    bottomSheetState: SheetState,
//    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    projectList: MutableState<List<ProjectListItem>>,
//    projectName: ProjectNameList,
    viewModel: ProjectViewModel,
    navigateToProjectDetails: (route: String, argument: ProjectListItem) -> Unit,
    navigateToAddProject: (route: String) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FabIcon(
                onFabClick = {
                    navigateToAddProject.invoke(Route.HomeNav.AddProject.route)
                }
            )
        }
    ) {
        if (projectList.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.no_project_available))
            }
        } else {
            Column {
                Row(
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    TextButton(onClick = {
//                    scope.launch { bottomSheetState.show() }
                        Toast.makeText(context, "Filter Clicked", Toast.LENGTH_LONG).show()
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
//                    viewModel.clearFilter()
                        Toast.makeText(context, "Filter Clear Clicked", Toast.LENGTH_LONG).show()
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
                    items(projectList.value) { projectItem ->
                        ProjectCard(project = projectItem) { project ->
                            navigateToProjectDetails.invoke(
                                Route.HomeNav.ProjectDetails.route,
                                project
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectCard(project: ProjectListItem, onClick: (project: ProjectListItem) -> Unit) {
    Card(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .clickable { onClick(project) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .background(colorResource(id = project.clientBackgroundColor))
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
                        text = project.client,
                        style = TextStyle(
                            color = colorResource(id = project.clientColor),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Text(
                    text = project.projectName,
                    style = TextStyle(
                        color = colorResource(id = R.color.gray_9),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                )

                Text(
                    text = project.projectStatus,
                    style = TextStyle(
                        color = colorResource(id = project.projectStatusColor),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .background(colorResource(id = project.projectStatusBackgroundColor))
                        .padding(8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (project.isSubProject) {
                    Text(
                        text = project.parentProjectName,
                        style = TextStyle(
                            color = colorResource(id = project.projectStatusColor),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .vertical()
                            .rotate(-90f)
                            .background(colorResource(id = project.projectStatusBackgroundColor))
                            .padding(8.dp)
                    )
                }

                // Allocated Resource Count
                Column(modifier = Modifier.fillMaxSize()) {
                    Column {
                        if (project.resourceTypeList.isEmpty()) {
                            NoResourceView()
                        } else {
                            FlowRow(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                project.resourceTypeList.forEach {
                                    CircularIndicatorView(
                                        progress = it.progress,
                                        text = it.designationType,
                                        indicatorText = "${it.allocationCount}/${it.totalCount}",
                                        indicatorColor = it.indicatorColor,
                                        indicatorBackgroundColor = it.indicatorBackgroundColor
                                    )
                                }
                            }
                        }
                    }

                    // Project Progress View
                    ProjectProgress(
                        startDate = project.projectStartDate,
                        endDate = project.projectEndDate
                    )
                }
            }
        }
    }
}

@Composable
private fun NoResourceView() {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "No Resource Assigned Yet",
            style = TextStyle(
                color = colorResource(id = R.color.gray_9),
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun ProjectProgress(startDate: Long, endDate: Long) {

    val totalTime = endDate.minus(startDate)
    val progressedTime = System.currentTimeMillis().minus(startDate)
    val progress = progressedTime.toFloat() / totalTime.toFloat()

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = startDate.millisToString(),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = endDate.millisToString(),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
        }

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(horizontal = 20.dp)
                .shadow(shape = CircleShape, elevation = 2.dp)
                .background(color = colorResource(id = R.color.red_1)),
            color = colorResource(id = R.color.red_6),
            progress = animatedProgress
        )
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }