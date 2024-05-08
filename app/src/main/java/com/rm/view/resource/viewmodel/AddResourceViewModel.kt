package com.rm.view.resource.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.rm.MainActivityViewModel
import com.rm.api.request.resource.AddResourceRequest
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.masterdata.Type
import com.rm.api.response.resource.AddResourceResponseData
import com.rm.base.BaseViewModel
import com.rm.data.resource.ResourceItemList
import com.rm.data.resource.ResourceProjectType
import com.rm.data.resource.UIAddResource
import com.rm.utils.ErrorUtils
import com.rm.utils.empty
import com.rm.view.resource.repository.ResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AddResourceViewModel @Inject constructor(
    private val repository: ResourceRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {

    val uiAddResource = UIAddResource()
    var editEnabled = false

    private val _addResourceResponse = MutableSharedFlow<Boolean>()
    val addResourceResponse = _addResourceResponse.asSharedFlow()

    fun addResource(
        masterData: MasterDataResponseData,
        mainViewModel: MainActivityViewModel
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.addResource(createAddResourceRequest(masterData)).let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _addResourceResponse.emit(true)
                }
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }

    private val _editResourceResponse = MutableSharedFlow<AddResourceResponseData>()
    val editResourceResponse = _editResourceResponse.asSharedFlow()

    fun editResource(
        masterData: MasterDataResponseData,
        mainViewModel: MainActivityViewModel
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.editResource(
            uiAddResource.resourceId.state.value,
            createAddResourceRequest(masterData)
        ).let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _editResourceResponse.emit(it)
                }
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }

    fun combineAndValidate(): Flow<Boolean> {
        return combine(
            uiAddResource.resourceName.state,
            uiAddResource.resourceType.state,
            uiAddResource.resourceDesignation.state,
            uiAddResource.resourceEmail.state,
            uiAddResource.resourceContactNumber.state,
            uiAddResource.resourceTechnology.state,
            uiAddResource.dateOfJoining.state,
            uiAddResource.resourcePassword.state,
            uiAddResource.linkedInProfile.state,
            uiAddResource.compensation.state,
        ) {
            validate()
        }
    }

    private fun validate(): Boolean {
        uiAddResource.validate(editEnabled)

        val hasError = uiAddResource.hasError()
        uiAddResource.enableSubmitButton.update { hasError.not() }
        return hasError
    }

    val openDateOfJoiningDialog: MutableState<Boolean> = mutableStateOf(false)
//    val openEndDateCalendarDialog: MutableState<Boolean> = mutableStateOf(false)

    fun openDateOfJoiningDialog() {
        openDateOfJoiningDialog.value = true
    }

    fun closeDateOfJoiningDialog() {
        openDateOfJoiningDialog.value = false
    }

    private fun createAddResourceRequest(
        masterData: MasterDataResponseData
    ): AddResourceRequest {
        var appUserRoleId = 0
        masterData.user_role_types.forEach {
            if (it.value.equals(uiAddResource.resourceType.state.value))
                appUserRoleId = it.id
        }

        var designationId = 0
        masterData.designation_types.forEach {
            if (it.value.equals(uiAddResource.resourceDesignation.state.value))
                designationId = it.id
        }

        val technologyIdList = mutableListOf<Int>()
        masterData.technologies.forEach { type ->
            val tempTech = ResourceProjectType(id = type.id.toString(), value = type.value)
            if (uiAddResource.resourceTechnology.state.value.contains(tempTech))
                technologyIdList.add(type.id)
        }

        return AddResourceRequest(
            appUserRoleId = appUserRoleId,
            contactNumber = uiAddResource.resourceContactNumber.state.value.ifEmpty { null },
            designation = designationId,
            email = uiAddResource.resourceEmail.state.value,
            name = uiAddResource.resourceName.state.value,
            password = uiAddResource.resourcePassword.state.value.ifEmpty { null },
            technologies = technologyIdList,
            dateOfJoining = null,
            linkedInProfile = null,
            compensation = null
        )
    }

    /* Populate Fields For Edit Resource */
    fun populateFields(resource: ResourceItemList, masterData: MasterDataResponseData) {
        editEnabled = true

        var userType = Type()
        masterData.user_role_types.forEach { type ->
            if (type.value.equals(resource.resourceType)) {
                userType = type
            }
        }

        var userDesignation = Type()
        masterData.designation_types.forEach { designation ->
            if (designation.value.equals(resource.designation)) {
                userDesignation = designation
            }
        }

        val tech = mutableListOf<Any>()
        resource.technology.forEach {
            masterData.technologies.forEach { type ->
                if (type.value.equals(it)) {
                    tech.add(ResourceProjectType(id = type.id.toString(), value = type.value))
                }
            }
        }

        uiAddResource.resourceId.state.value = resource.id
        uiAddResource.resourceName.state.value = resource.name
        uiAddResource.resourceType.state.value = userType
        uiAddResource.resourceDesignation.state.value = userDesignation
        uiAddResource.resourceEmail.state.value = resource.email
        uiAddResource.resourceContactNumber.state.value = resource.contactNumber
        uiAddResource.resourceTechnology.state.value = tech
        uiAddResource.dateOfJoining.state.value = resource.dateOfJoining
        uiAddResource.resourcePassword.state.value = String.empty()
        uiAddResource.linkedInProfile.state.value = resource.linkedInProfile
        uiAddResource.compensation.state.value = resource.compensation
    }
}