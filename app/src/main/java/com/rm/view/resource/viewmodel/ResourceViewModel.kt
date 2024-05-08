package com.rm.view.resource.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.rm.MainActivityViewModel
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.resource.toResourceItemList
import com.rm.base.BaseViewModel
import com.rm.data.resource.ResourceItemList
import com.rm.data.resource.ResourceProjectType
import com.rm.data.resource.ResourceSearchModelState
import com.rm.network.ErrorResponse
import com.rm.utils.ErrorUtils
import com.rm.view.resource.repository.ResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ResourceViewModel @Inject constructor(
    private val repository: ResourceRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {

    private val _resources = mutableStateListOf<ResourceItemList>()
    val resources: List<ResourceItemList>
        get() = _resources

    fun getResources(
        mainViewModel: MainActivityViewModel,
        masterData: MasterDataResponseData
    ) = launchWithViewModelScope(
        call = {
            mainViewModel.showLoader()
            repository.getResources()
                .let { response ->
                    if (response.isSuccessful) {
                        response.body()?.data?.let { resourceList ->
                            _resources.clear()
                            _resources.addAll(resourceList.resources.toResourceItemList(masterData))

                            allResources = resourceList.resources.toResourceItemList(masterData)
                        }
                    } else {
                        handleError(response)
                        mainViewModel.showErrorDialog()
                    }
                }
            mainViewModel.hideLoader()
        },
        exceptionCallback = { message ->
            handleError(ErrorResponse(message = message))
            mainViewModel.showErrorDialog()
        }
    )

    /* Filter */
    val selectedProjectList = mutableStateListOf<ResourceProjectType>()
    val selectedDesignationList = mutableStateListOf<ResourceProjectType>()
    val selectedTechnologyList = mutableStateListOf<ResourceProjectType>()

    fun clearFilter() = launchWithViewModelScope {
        selectedProjectList.clear()
        selectedDesignationList.clear()
        selectedTechnologyList.clear()
        _resources.clear()
        _resources.addAll(allResources)
    }

    fun filterResource() {
        val filteredProject = filterProject()
        val filteredDesignation = filterDesignation(filteredProject)
        val filteredTechnology = filterTechnology(filteredDesignation)
        _resources.clear()
        _resources.addAll(filteredTechnology)
    }

    private fun filterProject(): List<ResourceItemList> {
        if (selectedProjectList.isNotEmpty()) {
            val filteredProject = mutableListOf<ResourceItemList>()
            selectedProjectList.forEach { selectedProject ->
                val filter = allResources.filter { resource ->
                    // TODO: Need to check projectList in place of occupancy
                    resource.occupancy?.projectName?.contains(selectedProject.value, true) == true
                }
                filteredProject.addAll(filter)
            }
            return filteredProject
        } else {
            return allResources
        }
    }

    private fun filterDesignation(filteredList: List<ResourceItemList>): List<ResourceItemList> {
        if (selectedDesignationList.isNotEmpty()) {
            val filteredDesignation = mutableListOf<ResourceItemList>()
            selectedDesignationList.forEach { selectedDesignation ->
                val filter = filteredList.filter { resource ->
                    resource.designation.contains(selectedDesignation.value)
                }
                filteredDesignation.addAll(filter)
            }
            return filteredDesignation
        } else {
            return filteredList
        }
    }

    private fun filterTechnology(filteredList: List<ResourceItemList>): List<ResourceItemList> {
        if (selectedTechnologyList.isNotEmpty()) {
            val filteredTechnology = mutableListOf<ResourceItemList>()
            selectedTechnologyList.forEach { selectedTechnology ->
                val filter = filteredList.filter { resource ->
                    resource.technology.contains(selectedTechnology.value)
                }
                filteredTechnology.addAll(filter)
            }
            return filteredTechnology
        } else {
            return filteredList
        }
    }

    /* Search Resource */
    var allResources: List<ResourceItemList> = arrayListOf()
    private val searchText: MutableStateFlow<String> = MutableStateFlow("")
    private var showProgressBar: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var matchedResources: MutableStateFlow<List<ResourceItemList>> =
        MutableStateFlow(arrayListOf())

    val resourceSearchModelState = combine(
        searchText,
        matchedResources,
        showProgressBar
    ) { text, matchedResources, showProgress ->
        ResourceSearchModelState(
            text,
            matchedResources,
            showProgress
        )
    }

    fun onSearchTextChanged(changedSearchText: String) {
        searchText.value = changedSearchText
        if (changedSearchText.isEmpty()) {
            matchedResources.value = arrayListOf()
            return
        }
        val resourcesFromSearch = allResources.filter { x ->
            x.name.contains(changedSearchText, true)
//                    || x.email.contains(changedSearchText, true)
//                    || x.name.contains(changedSearchText, true)
        }
        matchedResources.value = resourcesFromSearch
    }

    fun onClearClick() {
        searchText.value = ""
        matchedResources.value = arrayListOf()
    }
}