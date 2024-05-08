@file:OptIn(ExperimentalMaterial3Api::class)

package com.rm.view.project

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.masterdata.Type
import com.rm.api.response.masterdata.toResourceProjectType
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.api.response.project.toProjectDetails
import com.rm.data.project.ProjectDetails
import com.rm.data.project.ProjectResourceTypeItem
import com.rm.data.project.UISubProject
import com.rm.data.resource.ResourceProjectType
import com.rm.navigation.Route
import com.rm.utils.dateToString
import com.rm.utils.empty
import com.rm.utils.getMutableStateValue
import com.rm.utils.millisToString
import com.rm.utils.stringToMillis
import com.rm.view.project.bottomsheet.AddResourceBottomSheetView
import com.rm.view.project.viewmodel.AddProjectViewModel
import com.rm.view.template.BottomView
import com.rm.view.template.DatePickerWithDateValidator
import com.rm.view.template.MultiSelectionChip
import com.rm.view.template.OutlinedLongButton
import com.rm.view.template.Space
import com.rm.view.template.SpaceTop
import com.rm.view.template.TitledCalendarFieldWithError
import com.rm.view.template.TitledExposedDropdownMenu
import com.rm.view.template.TitledOutlinedTextFieldWithError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentAddProjectScreen(
    editEnable: Boolean? = false,
    projectDetails: ProjectDetailsResponseData?,
    onBackPress: (route: String) -> Unit,
    onEditBackPress: (projectId: String, route: String) -> Unit,
    addProjectViewModel: AddProjectViewModel = hiltViewModel(),
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity)
) {
    val context = LocalContext.current
    val masterData = mainViewModel.masterData

    val scope = rememberCoroutineScope()
    val addResourceBottomSheetState = rememberModalBottomSheetState()

    val project = masterData?.let { projectDetails?.toProjectDetails(it) }
    projectDetails?.let { addProjectViewModel.projectDetailsResponse = it }

    LaunchedEffect(key1 = true) {
        addProjectViewModel.combineAndValidateProject().collectLatest {
            if (it) {
                Log.d("Validate", "Form has error: $it")
            }
        }
    }

    LaunchedEffect(key1 = true, block = {
        addProjectViewModel.projectAdded.collect {
            if (it) {
                Toast.makeText(context, "Project Added Successfully", Toast.LENGTH_LONG).show()
                onBackPress.invoke(Route.HomeNav.Project.route)
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        addProjectViewModel.projectUpdated.collect {
            if (it) {
                Toast.makeText(context, "Project Updated Successfully", Toast.LENGTH_LONG).show()
                onEditBackPress.invoke(
                    addProjectViewModel.selectedProjectId,
                    Route.HomeNav.ProjectDetails.route
                )
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        editEnable?.let {
            if (it) {
                masterData?.let { masterData ->
                    project?.let { project ->
                        addProjectViewModel.populateFields(
                            project = project,
                            masterData = masterData
                        )
                        addProjectViewModel.selectedProjectId = project.projectId
                    }
                }
            }
        }
    })

    Scaffold(
        topBar = {
            TopBar(stringResource(id = R.string.screen_add_project)) {
                onBackPress.invoke(Route.HomeNav.Project.route)
            }
        }
    ) { paddingValues ->
        masterData?.let { masterData ->
            InitView(
                paddingValues,
                editEnable,
                scope = scope,
                masterData = masterData,
                vm = addProjectViewModel,
                mainViewModel = mainViewModel,
                project = project,
                addResourceBSS = addResourceBottomSheetState,
                onBackPress = onBackPress,
                onEditBackPress = onEditBackPress
            )
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun InitView(
    paddingValues: PaddingValues,
    editEnable: Boolean? = false,
    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    project: ProjectDetails? = null,
    addResourceBSS: SheetState,
    vm: AddProjectViewModel,
    mainViewModel: MainActivityViewModel,
    onBackPress: (route: String) -> Unit,
    onEditBackPress: (projectId: String, route: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .weight(1f)
                .background(Color.White)
        ) {
            ContentView(
                editEnable = editEnable,
                scope = scope,
                masterData = masterData,
                addResourceBSS = addResourceBSS,
                vm = vm
            )
        }

        BottomView(
            leftButtonText = stringResource(id = R.string.label_cancel),
            rightButtonText = stringResource(id = R.string.label_submit),
            isRightButtonEnabled = getMutableStateValue(state = vm.uiAddProject.enableSubmitButton),
            onLeftButtonClick = {
                if (editEnable == true)
                    project?.let {
                        onEditBackPress.invoke(
                            vm.selectedProjectId,
                            Route.HomeNav.ProjectDetails.route
                        )
                    }
                else
                    onBackPress.invoke(Route.HomeNav.Project.route)
            },
            onRightButtonClick = {
                if (editEnable == true) {
                    vm.editProject(mainActivityViewModel = mainViewModel)
                } else {
                    vm.addProject(mainActivityViewModel = mainViewModel, masterData = masterData)
                }
            }
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ContentView(
    editEnable: Boolean? = false,
    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    addResourceBSS: SheetState,
    vm: AddProjectViewModel
) {
    val openStartDateDialog = remember { vm.openStartDateCalendarDialog }
    val openEndDateDialog = remember { vm.openEndDateCalendarDialog }

    val enteredProjectName =
        vm.uiAddProject.projectName.state.collectAsState(initial = String.empty())

    val selectedClient by remember {
        if (editEnable == true && masterData.clients.contains(vm.uiAddProject.projectClient.state.value)) {
            val index =
                masterData.clients.indexOf(vm.uiAddProject.projectClient.state.value)
            mutableStateOf(masterData.clients[index])
        } else {
            mutableStateOf(masterData.clients[0])
        }
    }

    var selectedProjectScope by remember { mutableStateOf(listOf<ResourceProjectType>()) }
    if (editEnable == true) {

        val list = mutableListOf<ResourceProjectType>()
        vm.uiAddProject.projectScope.state.value.forEach {
            val scope = it as Type
            list.add(ResourceProjectType(id = scope.id.toString(), value = scope.value))
        }
        selectedProjectScope = list
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize()
        ) {
            TitledOutlinedTextFieldWithError(
                title = R.string.label_project_name,
                uiTextField = vm.uiAddProject.projectName,
                hint = R.string.hint_name
            )

            TitledExposedDropdownMenu(
                title = R.string.label_client,
                items = masterData.clients,
                uiDropdownField = vm.uiAddProject.projectClient,
                selected = selectedClient,
                onItemSelected = {}
            )

            if (enteredProjectName.value.isNotEmpty()) {
                MultiSelectionChip(
                    textModifier = Modifier.padding(bottom = 20.dp),
                    title = R.string.label_project_scope,
                    list = masterData.project_scope_types.toResourceProjectType(),
                    selectedList = selectedProjectScope,
                    onSelectionChanged = { type ->
                        if (vm.uiAddProject.projectScope.state.value.contains(type)) {
                            vm.uiAddProject.projectScope.state.value.remove(type)
                            val v = vm.uiAddProject.projectSubProjectList.filter {
                                it.subProjectScope.state.value.contains(type)
                            }
                            vm.removeSubProject(v)
                        } else {
                            vm.uiAddProject.projectScope.state.value.add(type)
                            vm.addSubProject("${enteredProjectName.value} ${type.value}", type)
                        }

                        selectedProjectScope =
                            vm.uiAddProject.projectScope.state.value.filterIsInstance<ResourceProjectType>()
                    }
                )

                SpaceTop(top = 20.dp)
                ProjectDetailsView(
                    editEnable = editEnable,
                    scope = scope,
                    masterData = masterData,
                    addResourceBSS = addResourceBSS,
                    vm = vm
                )

                selectedProjectScope.forEachIndexed { index, projectScope ->
                    val subProject = vm.uiAddProject.projectSubProjectList[index]
                    SpaceTop(top = 20.dp)
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = colorResource(id = R.color.gray_6),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(20.dp)
                    ) {
                        SubProjectDetailsView(
                            editEnable = editEnable,
                            scope = scope,
                            masterData = masterData,
                            addResourceBSS = addResourceBSS,
                            vm = vm,
                            subProjectItem = subProject
                        )
                    }
                }
            }
        }

        // Date Picker
        if (openStartDateDialog.value) {
            val initialDate = remember {
                if (vm.isFromSubProject.value && vm.selectedSubProjectIndex.value > -1) {
                    vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value].subProjectStartDate.state.value
                } else {
                    vm.uiAddProject.projectStartDate.state.value
                }
            }
            DatePickerWithDateValidator(
                initialDateInMillis = if (initialDate.isNotEmpty()) initialDate.stringToMillis() else Calendar.getInstance().timeInMillis,
                onDismissRequest = { vm.closeStartDateCalendarDialog() },
                onConfirmClick = { selectedDate ->
                    if (vm.isFromSubProject.value && vm.selectedSubProjectIndex.value > -1) {
                        vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value].subProjectStartDate.state.value =
                            selectedDate.millisToString()
                    } else {
                        vm.uiAddProject.projectStartDate.state.value =
                            selectedDate.millisToString()
                    }
                    vm.closeStartDateCalendarDialog()
                },
                onDismissClick = { vm.closeStartDateCalendarDialog() }
            )
        }

        if (openEndDateDialog.value) {
            val initialDate = remember {
                if (vm.isFromSubProject.value && vm.selectedSubProjectIndex.value > -1) {
                    vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value].subProjectEndDate.state.value
                } else {
                    vm.uiAddProject.projectEndDate.state.value
                }
            }
            DatePickerWithDateValidator(
                initialDateInMillis = if (initialDate.isNotEmpty()) initialDate.stringToMillis() else Calendar.getInstance().timeInMillis,
                onDismissRequest = { vm.closeEndDateCalendarDialog() },
                onConfirmClick = { selectedDate ->
                    if (vm.isFromSubProject.value && vm.selectedSubProjectIndex.value > -1) {
                        vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value].subProjectEndDate.state.value =
                            selectedDate.millisToString()
                    } else {
                        vm.uiAddProject.projectEndDate.state.value =
                            selectedDate.millisToString()
                    }
                    vm.closeEndDateCalendarDialog()
                },
                onDismissClick = { vm.closeEndDateCalendarDialog() }
            )
        }
    }



    if (addResourceBSS.isVisible) {
        AddResourceBottomSheetView(
            addResourceBottomSheetState = addResourceBSS,
            scope = scope,
            designationTypeList = masterData.designation_types,
            onDoneClick = { selectedDesignation, count ->
                if (vm.isFromSubProject.value && (vm.selectedSubProjectIndex.value > -1)) {
                    val sProject =
                        vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value]
                    sProject.subProjectResourceTypeList.state.value.add(
                        ProjectResourceTypeItem(
                            designationId = selectedDesignation.id,
                            designationName = selectedDesignation.value,
                            count = count
                        )
                    )
                } else {
                    vm.uiAddProject.projectResourceTypeList.state.value.add(
                        ProjectResourceTypeItem(
                            designationId = selectedDesignation.id,
                            designationName = selectedDesignation.value,
                            count = count
                        )
                    )
                }
            }
        )
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SubProjectDetailsView(
    editEnable: Boolean? = false,
    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    addResourceBSS: SheetState,
    vm: AddProjectViewModel,
    subProjectItem: UISubProject = UISubProject()
) {
    val subProjectName =
        subProjectItem.subProjectName.state.collectAsState(initial = String.empty())

    vm.setIsFromSubProjectTrue()

    val selectedProjectState by remember {
        if (editEnable == true && masterData.project_states.contains(vm.uiAddProject.projectState.state.value)) {
            val index =
                masterData.project_states.indexOf(vm.uiAddProject.projectState.state.value)
            mutableStateOf(masterData.project_states[index])
        } else {
            mutableStateOf(masterData.project_states[0])
        }
    }

    val projectResourceList = remember {
        mutableStateOf(subProjectItem.subProjectResourceTypeList.state.value)
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        text = subProjectName.value,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.gray_9),
            letterSpacing = 2.sp
        )
    )

    TitledOutlinedTextFieldWithError(
        title = R.string.label_project_description,
        uiTextField = subProjectItem.subProjectDescription,
        hint = R.string.hint_description,
        singleLine = false,
        lineCount = 4,
        maxChar = 200
    )

    TitledExposedDropdownMenu(
        title = R.string.label_project_state,
        items = masterData.project_states,
        uiDropdownField = subProjectItem.subProjectState,
        selected = selectedProjectState,
        onItemSelected = {}
    )

    TitledCalendarFieldWithError(
        title = R.string.label_project_start_date,
        uiTextField = subProjectItem.subProjectStartDate,
        hint = Calendar.getInstance().time.dateToString(),
        onCalendarClick = {
            vm.selectedSubProjectIndex.value =
                vm.uiAddProject.projectSubProjectList.indexOf(subProjectItem)
            vm.setIsFromSubProjectTrue()
            vm.openStartDateCalendarDialog()
        }
    )

    TitledCalendarFieldWithError(
        title = R.string.label_project_end_date,
        uiTextField = subProjectItem.subProjectEndDate,
        hint = Calendar.getInstance().time.dateToString(),
        onCalendarClick = {
            vm.selectedSubProjectIndex.value =
                vm.uiAddProject.projectSubProjectList.indexOf(subProjectItem)
            vm.setIsFromSubProjectTrue()
            vm.openEndDateCalendarDialog()
        }
    )

    if (projectResourceList.value.isNotEmpty()) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.gray_6),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_resource),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 2.sp
                )
            )

            SpaceTop(top = 8.dp)
            projectResourceList.value.forEach { resource ->
                ResourceTypeList(resource = resource as ProjectResourceTypeItem) {
                    val tempResourceList = mutableListOf<Any>()
                    tempResourceList.addAll(subProjectItem.subProjectResourceTypeList.state.value)
                    tempResourceList.remove(resource)
                    subProjectItem.subProjectResourceTypeList.state.value = tempResourceList
                    projectResourceList.value = tempResourceList
                }
            }
        }
    }

    OutlinedLongButton(buttonText = R.string.label_project_add_resource) {
        scope.launch {
            vm.selectedSubProjectIndex.value =
                vm.uiAddProject.projectSubProjectList.indexOf(subProjectItem)
            addResourceBSS.show()
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ProjectDetailsView(
    editEnable: Boolean? = false,
    scope: CoroutineScope,
    masterData: MasterDataResponseData,
    addResourceBSS: SheetState,
    vm: AddProjectViewModel
) {

    vm.setIsFromSubProjectFalse()

    val selectedProjectState by remember {
        if (editEnable == true && masterData.project_states.contains(vm.uiAddProject.projectState.state.value)) {
            val index =
                masterData.project_states.indexOf(vm.uiAddProject.projectState.state.value)
            mutableStateOf(masterData.project_states[index])
        } else {
            mutableStateOf(masterData.project_states[0])
        }
    }

    var projectResourceList by remember {
        mutableStateOf(vm.uiAddProject.projectResourceTypeList.state.value)
    }


    TitledOutlinedTextFieldWithError(
        title = R.string.label_project_description,
        uiTextField = vm.uiAddProject.projectDescription,
        hint = R.string.hint_description,
        singleLine = false,
        lineCount = 4,
        maxChar = 200
    )

    TitledExposedDropdownMenu(
        title = R.string.label_project_state,
        items = masterData.project_states,
        uiDropdownField = vm.uiAddProject.projectState,
        selected = selectedProjectState,
        onItemSelected = {}
    )

    TitledCalendarFieldWithError(
        title = R.string.label_project_start_date,
        uiTextField = vm.uiAddProject.projectStartDate,
        hint = Calendar.getInstance().time.dateToString(),
        onCalendarClick = {
            vm.setIsFromSubProjectFalse()
            vm.openStartDateCalendarDialog()
        }
    )

    TitledCalendarFieldWithError(
        title = R.string.label_project_end_date,
        uiTextField = vm.uiAddProject.projectEndDate,
        hint = Calendar.getInstance().time.dateToString(),
        onCalendarClick = {
            vm.setIsFromSubProjectFalse()
            vm.openEndDateCalendarDialog()
        }
    )

    if (projectResourceList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.gray_6),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.label_resource),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 2.sp
                )
            )

            SpaceTop(top = 8.dp)
            projectResourceList.forEach { resource ->
                ResourceTypeList(resource = resource as ProjectResourceTypeItem) {
                    val tempResourceList = mutableListOf<Any>()
                    tempResourceList.addAll(vm.uiAddProject.projectResourceTypeList.state.value)
                    tempResourceList.remove(resource)
                    vm.uiAddProject.projectResourceTypeList.state.value = tempResourceList
                    projectResourceList = tempResourceList
                }
            }
        }
    }

    OutlinedLongButton(buttonText = R.string.label_project_add_resource) {
        scope.launch { addResourceBSS.show() }
    }
}

@Composable
fun ResourceTypeList(
    resource: ProjectResourceTypeItem,
    deleteResource: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = resource.designationName,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.gray_9),
                letterSpacing = 0.75.sp
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = resource.count.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.gray_9),
                    letterSpacing = 0.75.sp
                )
            )

            Space(start = 8.dp)
            IconButton(onClick = { deleteResource() }) {
                Icon(
                    modifier = Modifier.height(20.dp),
                    painter = painterResource(id = R.drawable.ic_delete_20),
                    tint = colorResource(id = R.color.red_6),
                    contentDescription = stringResource(id = R.string.content_desc_delete)
                )
            }
        }
    }
}