package com.rm.view.project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.rm.MainActivityViewModel
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.project.ProjectDetailsResponseData
import com.rm.api.response.project.toProjectDetails
import com.rm.base.BaseViewModel
import com.rm.data.project.ProjectDetails
import com.rm.data.project.toProjectResource
import com.rm.data.project.toSubProjectResource
import com.rm.utils.ErrorUtils
import com.rm.utils.empty
import com.rm.view.project.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
    private val repository: ProjectRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {
    var isFromSubProject = false
    var subProjectIndex = 0
    var selectedIndex = 0

    var resourceEdited = mutableStateOf(false)

    var selectedProjectId = String.empty()

    var _projectDetailsResponse: MutableState<ProjectDetailsResponseData> = mutableStateOf(
        ProjectDetailsResponseData()
    )

    private var _projectDetail = mutableStateOf(ProjectDetails())
    val projectDetail: MutableState<ProjectDetails>
        get() = _projectDetail

    fun getProjectDetail(
        mainViewModel: MainActivityViewModel,
        masterData: MasterDataResponseData
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.getProjectDetails(projectId = selectedProjectId).let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let { detail ->
                    _projectDetailsResponse.value = detail
                    _projectDetail.value = detail.toProjectDetails(masterData = masterData)
                }
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }

    private val _projectDeleted: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val projectDeleted = _projectDeleted.asSharedFlow()

    fun deleteProject(
        mainViewModel: MainActivityViewModel
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.deleteProject(projectId = selectedProjectId).let { response ->
            if (response.isSuccessful) {
                _projectDeleted.emit(true)
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }

    private val _projectUpdated: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val projectUpdated = _projectUpdated.asSharedFlow()

    fun updateProject(
        mainViewModel: MainActivityViewModel,
        masterData: MasterDataResponseData
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.updateProject(
            projectId = selectedProjectId,
            request = createUpdateProjectRequest(masterData = masterData)//_projectDetailsResponse.value
        ).let { response ->
            if (response.isSuccessful) {
                _projectUpdated.emit(true)
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }

    /* Create Update Project Request */
    private fun createUpdateProjectRequest(masterData: MasterDataResponseData): ProjectDetailsResponseData {

        _projectDetailsResponse.value.apply {
            project_resources.clear()
            _projectDetail.value.projectResourceList.forEach {
                project_resources.addAll(it.resourceList.toProjectResource(masterData))
            }

            _projectDetail.value.subProjectList.forEachIndexed { index, subProject ->
                sub_projects[index].sub_project_resources.clear()
                subProject.subProjectResourceList.forEach { projectResources ->
                    sub_projects[index].sub_project_resources.addAll(projectResources.resourceList.toSubProjectResource(masterData))
                }
            }
        }

        return _projectDetailsResponse.value
    }
}