package com.rm.view.resource

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.rm.data.resource.Occupancy
import com.rm.data.resource.ResourceItemList
import com.rm.navigation.Route
import com.rm.view.resource.viewmodel.ResourceDetailsViewModel
import com.rm.view.template.BottomView
import com.rm.view.template.SpaceTop

@Composable
fun ComponentResourceDetailsScreen(
    resourceId: String?,
    viewModel: ResourceDetailsViewModel = hiltViewModel(),
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity),
    navigateToAddResource: (route: String, editEnable: Boolean, resource: ResourceItemList) -> Unit,
    onBackPress: (route: String) -> Unit
) {
    val context = LocalContext.current
    val masterData = mainViewModel.masterData

    resourceId?.let { id ->
        viewModel.selectedResourceId = id
    }

    LaunchedEffect(key1 = true, block = {
        masterData?.let { masterData ->
            viewModel.getResourceDetail(masterData = masterData, mainViewModel)
        }
    })

    LaunchedEffect(key1 = true, block = {
        viewModel.deleteResourceResponse.collect {
            if (it) {
                Toast.makeText(context, "Resource Deleted Successfully", Toast.LENGTH_LONG).show()
                onBackPress.invoke(Route.HomeNav.Resource.route)
            }
        }
    })

    val resource = remember { viewModel.resourceDetail }

    Scaffold(
        topBar = {
            TopBar("Resource Details") {
                onBackPress.invoke(Route.HomeNav.Resource.route)
            }
        }
    ) { paddingValues ->
        InitResourceDetailView(
            navigateToAddResource,
            context,
            viewModel,
            mainViewModel,
            resource.value,
            paddingValues
        )
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
fun InitResourceDetailView(
    navigateToAddResource: (route: String, editEnable: Boolean, resource: ResourceItemList) -> Unit,
    context: Context,
    viewModel: ResourceDetailsViewModel,
    mainViewModel: MainActivityViewModel,
    resource: ResourceItemList?,
    paddingValues: PaddingValues
) {
    resource?.let {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .padding(24.dp)
                        .size(180.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
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
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.gray_9),
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Text(
                    text = "(${resource.designation})",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.gray_9),
                        fontWeight = FontWeight.Bold
                    )
                )

                Row() {
                    Text(
                        text = resource.email,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.gray_9),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (resource.email.isNotEmpty() && resource.contactNumber.isNotEmpty()) {
                        Text(
                            text = "    •    ",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.gray_9),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = resource.contactNumber,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.gray_9),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row() {
                    resource.technology.forEachIndexed { index, tech ->
                        Text(
                            text = tech,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.gray_9),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if ((resource.technology.size - 1 > index)) {
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

                SpaceTop(top = 24.dp)
                if (resource.projectList.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        NoOccupancyCard()
                    }
                } else {
                    resource.projectList.forEach { project ->
                        Card(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                            // backgroundColor = colorResource(id = project.occupancyBackgroundColor)
                        ) {
                            OccupancyCard(resourceOccupancy = project)
                        }
                    }
                }
            }

            BottomView(
                leftButtonText = stringResource(id = R.string.label_delete),
                rightButtonText = stringResource(id = R.string.label_edit),
                onLeftButtonClick = {
                    viewModel.deleteResource(resourceId = resource.id, mainViewModel)
                },
                onRightButtonClick = {
                    navigateToAddResource.invoke(Route.HomeNav.AddResource.route, true, resource)
                }
            )
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
        Column() {
            Text(
                text = resourceOccupancy.projectName,
                style = TextStyle(
                    color = colorResource(id = R.color.gray_9),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Client - HAVAS",
                style = TextStyle(
                    color = colorResource(id = R.color.gray_9),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "Delivery Date - 20 Dec 2023",
                style = TextStyle(
                    color = colorResource(id = R.color.gray_9),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
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
}