package com.rm.view.resource

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.R
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.masterdata.toResourceProjectType
import com.rm.api.response.resource.toResourceItemList
import com.rm.data.resource.ResourceItemList
import com.rm.data.resource.ResourceProjectType
import com.rm.navigation.Route
import com.rm.utils.dateToString
import com.rm.utils.getMutableStateValue
import com.rm.utils.millisToString
import com.rm.utils.stringToMillis
import com.rm.view.resource.viewmodel.AddResourceViewModel
import com.rm.view.template.*
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@Composable
fun ComponentAddResourceScreen(
    editEnable: Boolean? = false,
    resource: ResourceItemList? = null,
    onBackPress: (route: String) -> Unit,
    onEditBackPress: (resource: ResourceItemList, route: String) -> Unit,
    addResourceVM: AddResourceViewModel = hiltViewModel(),
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity)
) {
    val context = LocalContext.current
    val masterData = mainViewModel.masterData

    LaunchedEffect(key1 = true) {
        addResourceVM.combineAndValidate().collectLatest {
            if (it) {
                Log.d("Validate", "Form has error: $it")
            }
        }
    }

    LaunchedEffect(key1 = true, block = {
        addResourceVM.addResourceResponse.collectLatest {
            if (it) {
                // On Success Navigate Back
                Toast.makeText(context, "Resource added successfully", Toast.LENGTH_LONG)
                    .show()
                onBackPress.invoke(Route.HomeNav.Resource.route)
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        addResourceVM.editResourceResponse.collectLatest { response ->
            // On Success Navigate Back
            resource?.let { resourceItem ->
                val editedResource =
                    response.toResourceItemList(resource = resourceItem, masterData = masterData)

                Toast.makeText(context, "Resource Edited successfully", Toast.LENGTH_LONG)
                    .show()
                onEditBackPress.invoke(editedResource, Route.HomeNav.ResourceDetails.route)
            }
        }
    })

    Scaffold(
        topBar = {
            TopBar(stringResource(id = R.string.screen_add_resource)) {
                onBackPress.invoke(Route.HomeNav.Resource.route)
            }
        }
    ) { paddingValues ->
        masterData?.let { masterData ->
            resource?.let { resource ->
                addResourceVM.populateFields(resource = resource, masterData = masterData)
            }
            InitView(
                paddingValues,
                editEnable,
                masterData = masterData,
                vm = addResourceVM,
                mainViewModel = mainViewModel,
                resource = resource,
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

@Composable
fun InitView(
    paddingValues: PaddingValues,
    editEnable: Boolean? = false,
    masterData: MasterDataResponseData,
    resource: ResourceItemList? = null,
    vm: AddResourceViewModel,
    mainViewModel: MainActivityViewModel,
    onBackPress: (route: String) -> Unit,
    onEditBackPress: (resource: ResourceItemList, route: String) -> Unit
) {
    val dateOfJoiningDialog = remember { vm.openDateOfJoiningDialog }

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .weight(1f)
                .background(Color.White)
        ) {
            ContentView(
                editEnable = editEnable,
                masterData = masterData,
                vm = vm
            )
        }

        BottomView(
            leftButtonText = stringResource(id = R.string.label_cancel),
            rightButtonText = stringResource(id = R.string.label_submit),
            isRightButtonEnabled = getMutableStateValue(state = vm.uiAddResource.enableSubmitButton),
            onLeftButtonClick = {
                if (editEnable == true)
                    resource?.let {
                        onEditBackPress.invoke(it, Route.HomeNav.ResourceDetails.route)
                    }
                else
                    onBackPress.invoke(Route.HomeNav.Resource.route)
            },
            onRightButtonClick = {
                if (editEnable == true) {
                    vm.editResource(masterData = masterData, mainViewModel)
                } else {
                    vm.addResource(masterData = masterData, mainViewModel)
                }
            }
        )
    }

    // Date Picker
    if (dateOfJoiningDialog.value) {
        val initialDate = remember {
//            if (vm.isFromSubProject.value && vm.selectedSubProjectIndex.value > -1) {
//                vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value].subProjectStartDate.state.value
//            } else {
                vm.uiAddResource.dateOfJoining.state.value
//            }
        }
        DatePickerWithDateValidator(
            initialDateInMillis = if (initialDate.isNotEmpty()) initialDate.stringToMillis() else Calendar.getInstance().timeInMillis,
            onDismissRequest = { vm.closeDateOfJoiningDialog() },
            onConfirmClick = { selectedDate ->
//                if (vm.isFromSubProject.value && vm.selectedSubProjectIndex.value > -1) {
//                    vm.uiAddProject.projectSubProjectList[vm.selectedSubProjectIndex.value].subProjectStartDate.state.value =
//                        selectedDate.millisToString()
//                } else {
                    vm.uiAddResource.dateOfJoining.state.value =
                        selectedDate.millisToString()
//                }
                vm.closeDateOfJoiningDialog()
            },
            onDismissClick = { vm.closeDateOfJoiningDialog() }
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ContentView(
    editEnable: Boolean? = false,
    masterData: MasterDataResponseData,
    vm: AddResourceViewModel
) {
    val selectedUserRoleType by remember {
        if (editEnable == true && masterData.user_role_types.contains(vm.uiAddResource.resourceType.state.value)) {
            val index =
                masterData.user_role_types.indexOf(vm.uiAddResource.resourceType.state.value)
            mutableStateOf(masterData.user_role_types[index])
        } else {
            mutableStateOf(masterData.user_role_types[0])
        }
    }

    val selectedDesignationType by remember {
        if (editEnable == true && masterData.designation_types.contains(vm.uiAddResource.resourceDesignation.state.value)) {
            val index =
                masterData.designation_types.indexOf(vm.uiAddResource.resourceDesignation.state.value)
            mutableStateOf(masterData.designation_types[index])
        } else {
            mutableStateOf(masterData.designation_types.get(0))
        }
    }

    var selectedTechnologyList by remember { mutableStateOf(listOf<ResourceProjectType>()) }
    if (editEnable == true) {
        val tempList = mutableListOf<ResourceProjectType>()
        vm.uiAddResource.resourceTechnology.state.value.forEach {
            val tech = it as ResourceProjectType
            tempList.add(ResourceProjectType(id = tech.id.toString(), value = tech.value))
        }
        selectedTechnologyList = tempList
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize()
    ) {
        TitledOutlinedTextFieldWithError(
            title = R.string.label_name,
            uiTextField = vm.uiAddResource.resourceName,
            hint = R.string.hint_name
        )

        TitledExposedDropdownMenu(
            title = R.string.label_type,
            items = masterData.user_role_types,
            uiDropdownField = vm.uiAddResource.resourceType,
            selected = selectedUserRoleType,
            onItemSelected = {}
        )

        TitledExposedDropdownMenu(
            title = R.string.label_designation,
            items = masterData.designation_types,
            uiDropdownField = vm.uiAddResource.resourceDesignation,
            selected = selectedDesignationType,
            onItemSelected = {}
        )

        TitledOutlinedTextFieldWithError(
            title = R.string.label_email,
            uiTextField = vm.uiAddResource.resourceEmail,
            hint = R.string.hint_email
        )

        TitledOutlinedTextFieldWithError(
            title = R.string.label_contact,
            uiTextField = vm.uiAddResource.resourceContactNumber,
            hint = R.string.hint_contact
        )

        MultiSelectionChip(
            textModifier = Modifier.padding(bottom = 20.dp),
            title = R.string.label_technology,
            list = masterData.technologies.toResourceProjectType(),
            selectedList = selectedTechnologyList,
            onSelectionChanged = {
                val oldList: MutableList<ResourceProjectType> =
                    selectedTechnologyList.toMutableList()
                val techType = it

                if (oldList.contains(techType)) {
                    oldList.remove(techType)
                } else {
                    oldList.add(techType)
                }

                selectedTechnologyList = oldList
                vm.uiAddResource.resourceTechnology.state.value.clear()
                vm.uiAddResource.resourceTechnology.state.value.addAll(selectedTechnologyList)
            }
        )

        SpaceTop(top = 20.dp)
//        TitledOutlinedTextFieldWithError(
//            title = R.string.label_date_of_joining,
//            uiTextField = vm.uiAddResource.dateOfJoining,
//            hint = R.string.hint_date_of_joining
//        )

        TitledCalendarFieldWithError(
            title = R.string.label_date_of_joining,
            uiTextField = vm.uiAddResource.dateOfJoining,
            hint = Calendar.getInstance().time.dateToString(),
            onCalendarClick = {
//                vm.selectedSubProjectIndex.value =
//                    vm.uiAddProject.projectSubProjectList.indexOf(subProjectItem)
//                vm.setIsFromSubProjectTrue()
                vm.openDateOfJoiningDialog()
            }
        )

        TitledOutlinedTextFieldWithError(
            title = R.string.label_password,
            uiTextField = vm.uiAddResource.resourcePassword,
            hint = R.string.hint_password
        )

        TitledOutlinedTextFieldWithError(
            title = R.string.label_linked_in_profile,
            uiTextField = vm.uiAddResource.linkedInProfile,
            hint = R.string.hint_linked_in_profile
        )

        TitledOutlinedTextFieldWithError(
            title = R.string.label_compensation,
            uiTextField = vm.uiAddResource.compensation,
            hint = R.string.hint_compensation
        )
    }
}