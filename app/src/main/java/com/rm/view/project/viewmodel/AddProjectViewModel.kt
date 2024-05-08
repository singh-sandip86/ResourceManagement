package com.rm.view.project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.rm.MainActivityViewModel
import com.rm.api.request.project.AddProjectRequest
import com.rm.api.request.project.ResourceType
import com.rm.api.request.project.SubProject
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.masterdata.Type
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.base.BaseViewModel
import com.rm.data.project.ProjectDetails
import com.rm.data.project.ProjectResourceTypeItem
import com.rm.data.project.UIAddProject
import com.rm.data.project.UISubProject
import com.rm.data.resource.ResourceProjectType
import com.rm.utils.ErrorUtils
import com.rm.utils.empty
import com.rm.utils.millisToString
import com.rm.utils.stringToMillis
import com.rm.view.project.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {

    var selectedProjectId = String.empty()
    var projectDetailsResponse = ProjectDetailsResponseData()

    // API Call
    private val _projectAdded: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val projectAdded = _projectAdded.asSharedFlow()

    fun addProject(
        mainActivityViewModel: MainActivityViewModel,
        masterData: MasterDataResponseData
    ) = launchWithViewModelScope {
        mainActivityViewModel.showLoader()
        projectRepository.addProject(createAddProjectRequest(masterData)).let { response ->
            if (response.isSuccessful) {
                _projectAdded.emit(true)
            } else {
                handleError(response)
                mainActivityViewModel.showErrorDialog()
            }
            mainActivityViewModel.hideLoader()
        }
    }

    private val _projectUpdated: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val projectUpdated = _projectUpdated.asSharedFlow()

    fun editProject(
        mainActivityViewModel: MainActivityViewModel
    ) = launchWithViewModelScope {
        mainActivityViewModel.showLoader()
        projectRepository.updateProject(
            projectId = selectedProjectId,
            request = createUpdateProjectRequest()
        ).let { response ->
            if (response.isSuccessful) {
                _projectUpdated.emit(true)
            } else {
                handleError(response)
                mainActivityViewModel.showErrorDialog()
            }
            mainActivityViewModel.hideLoader()
        }
    }


    var editEnabled = false
    val openStartDateCalendarDialog: MutableState<Boolean> = mutableStateOf(false)
    val openEndDateCalendarDialog: MutableState<Boolean> = mutableStateOf(false)

    fun openStartDateCalendarDialog() {
        openStartDateCalendarDialog.value = true
    }

    fun closeStartDateCalendarDialog() {
        openStartDateCalendarDialog.value = false
    }

    fun openEndDateCalendarDialog() {
        openEndDateCalendarDialog.value = true
    }

    fun closeEndDateCalendarDialog() {
        openEndDateCalendarDialog.value = false
    }

    val uiAddProject = UIAddProject()

    fun combineAndValidateProject(): Flow<Boolean> {
        return combine(
            uiAddProject.projectId.state,
            uiAddProject.projectName.state,
            uiAddProject.projectClient.state,
            uiAddProject.projectDescription.state,
            uiAddProject.projectState.state,
            uiAddProject.projectScope.state,
            uiAddProject.projectStartDate.state,
            uiAddProject.projectEndDate.state,
            uiAddProject.projectResourceTypeList.state
        ) {
            validateProject()
        }
    }

    private fun validateProject(): Boolean {
        uiAddProject.validateProject()

        val hasError = uiAddProject.hasError()
        uiAddProject.enableSubmitButton.update { hasError.not() }
        return hasError
    }

    fun addSubProject(name: String, scope: Any) {
        val subProject = UISubProject()
        subProject.subProjectName.state.value = name
        subProject.subProjectScope.state.value.add(scope)
        uiAddProject.projectSubProjectList.add(subProject)
    }

    fun removeSubProject(subProjectList: List<UISubProject>) {
        uiAddProject.projectSubProjectList.removeAll(subProjectList)
    }

    val isFromSubProject: MutableState<Boolean> = mutableStateOf(false)
    val selectedSubProjectIndex: MutableState<Int> = mutableStateOf(-1)

    fun setIsFromSubProjectTrue() {
        isFromSubProject.value = true
    }

    fun setIsFromSubProjectFalse() {
        isFromSubProject.value = false
    }

    /* Populate Fields For Edit Project */
    fun populateFields(
        project: ProjectDetails,
        masterData: MasterDataResponseData
    ) {
        editEnabled = true

        uiAddProject.projectId.state.value = project.projectId
        uiAddProject.projectName.state.value = project.projectName
        uiAddProject.projectClient.state.value =
            Type(id = project.clientId, value = project.clientName)

        masterData.project_scope_types.forEach {
            if (project.projectType.contains(it.value)) {
                uiAddProject.projectScope.state.value.add(it)
            }
        }

        uiAddProject.projectDescription.state.value = project.projectDescription
        masterData.project_states.forEach {
            if (it.value == project.projectStatus) {
                uiAddProject.projectState.state.value = it
            }
        }

        uiAddProject.projectStartDate.state.value = project.projectStartDate.millisToString()
        uiAddProject.projectEndDate.state.value = project.projectEndDate.millisToString()

        project.projectResourceList.forEach {
            val projectResource = ProjectResourceTypeItem(
                designationId = it.resourceTypeId,
                designationName = it.resourceType,
                count = it.totalCount
            )
            uiAddProject.projectResourceTypeList.state.value.add(projectResource)
        }

        project.subProjectList.forEach {
            val subProject = UISubProject()
            subProject.subProjectId.state.value = it.subProjectId
            subProject.subProjectName.state.value = it.subProjectName
            subProject.subProjectDescription.state.value = it.subProjectDescription

            masterData.project_scope_types.forEach { type ->
                if (it.subProjectType.contains(type.value)) {
                    subProject.subProjectScope.state.value.add(it)
                }
            }

            masterData.project_states.forEach { type ->
                if (type.value == it.subProjectStatus) {
                    subProject.subProjectState.state.value = type
                }
            }

            subProject.subProjectStartDate.state.value = it.subProjectStartDate.millisToString()
            subProject.subProjectEndDate.state.value = it.subProjectEndDate.millisToString()

            it.subProjectResourceList.forEach { subProjectResource ->
                val projectResource = ProjectResourceTypeItem(
                    designationId = subProjectResource.resourceTypeId,
                    designationName = subProjectResource.resourceType,
                    count = subProjectResource.totalCount
                )
                subProject.subProjectResourceTypeList.state.value.add(projectResource)
            }

            uiAddProject.projectSubProjectList.add(subProject)
        }
    }

    // Create Add Project Request
    private fun createAddProjectRequest(masterData: MasterDataResponseData): AddProjectRequest {

        var client = 0
        masterData.clients.forEach {
            if (it.value == uiAddProject.projectClient.state.value) {
                client = it.id
            }
        }

        val projectScope = mutableListOf<Int>()
        uiAddProject.projectScope.state.value.forEach {
            val scope = it as ResourceProjectType
            if (masterData.project_scope_types.contains(
                    Type(
                        id = scope.id.toInt(),
                        value = scope.value
                    )
                )
            ) {
                projectScope.add(scope.id.toInt())
            }
        }

        var projectState = 0
        masterData.project_states.forEach {
            if (it.value == uiAddProject.projectState.state.value) {
                projectState = it.id
            }
        }

        val resourceType = mutableListOf<ResourceType>()
        uiAddProject.projectResourceTypeList.state.value.forEach {
            val type = it as ProjectResourceTypeItem
            resourceType.add(
                ResourceType(
                    count = type.count,
                    designation_id = type.designationId
                )
            )
        }

        val subProjectList = mutableListOf<SubProject>()
        uiAddProject.projectSubProjectList.forEach { subProject ->

            var subProjectState = 0
            masterData.project_states.forEach {
                if (it.value == subProject.subProjectState.state.value) {
                    subProjectState = it.id
                }
            }

            var subProjectScope = 0
            subProject.subProjectScope.state.value.forEach {
                val scope = it as ResourceProjectType
                if (masterData.project_scope_types.contains(
                        Type(
                            id = scope.id.toInt(),
                            value = scope.value
                        )
                    )
                ) {
                    subProjectScope = scope.id.toInt()
                }
            }

            val subProjectResourceType = mutableListOf<ResourceType>()
            subProject.subProjectResourceTypeList.state.value.forEach {
                val type = it as ProjectResourceTypeItem
                subProjectResourceType.add(
                    ResourceType(
                        count = type.count,
                        designation_id = type.designationId
                    )
                )
            }

            subProjectList.add(
                SubProject(
                    project_name = subProject.subProjectName.state.value,
                    project_description = subProject.subProjectDescription.state.value,
                    project_state = subProjectState,
                    project_scope_id = subProjectScope,
                    start_date = subProject.subProjectStartDate.state.value.stringToMillis(),
                    end_date = subProject.subProjectEndDate.state.value.stringToMillis(),
                    resource_type = subProjectResourceType,
                    sub_project_resources = listOf() // This is sent blank as we are not adding any resource here
                )
            )
        }

        return AddProjectRequest(
            client = client,
            project_name = uiAddProject.projectName.state.value,
            project_description = uiAddProject.projectDescription.state.value,
            project_resources = listOf(), // This is sent blank as we are not adding any resource here
            project_scope = projectScope,
            project_state = projectState,
            start_date = uiAddProject.projectStartDate.state.value.stringToMillis(),
            end_date = uiAddProject.projectEndDate.state.value.stringToMillis(),
            resource_type = resourceType, // Just the resource type is added
            sub_projects = subProjectList
        )
    }

    private fun createUpdateProjectRequest(): ProjectDetailsResponseData {

        projectDetailsResponse.apply {
            resource_type.clear()
            uiAddProject.projectResourceTypeList.state.value.forEach {
                val resType = it as ProjectResourceTypeItem
                resource_type.add(
                    com.rm.api.response.project.ResourceType(
                        designation_id = resType.designationId,
                        count = resType.count
                    )
                )
            }

            uiAddProject.projectSubProjectList.forEachIndexed { index, uiSubProject ->
                sub_projects[index].resource_type.clear()
                uiSubProject.subProjectResourceTypeList.state.value.forEach {
                    val resType = it as ProjectResourceTypeItem
                    sub_projects[index].resource_type.add(
                        com.rm.api.response.project.ResourceType(
                            designation_id = resType.designationId,
                            count = resType.count
                        )
                    )
                }
            }
        }

        return projectDetailsResponse
    }
}