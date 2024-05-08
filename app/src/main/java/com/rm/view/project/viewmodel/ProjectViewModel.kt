package com.rm.view.project.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.rm.MainActivityViewModel
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.project.toProjectListItem
import com.rm.base.BaseViewModel
import com.rm.data.project.ProjectListItem
import com.rm.utils.ErrorUtils
import com.rm.view.project.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository,
    errorUtils: ErrorUtils
): BaseViewModel(errorUtils) {

    private val _projects = mutableStateListOf<ProjectListItem>()
    val projects: List<ProjectListItem>
        get() = _projects

    fun getProjects(
        mainViewModel: MainActivityViewModel,
        masterData: MasterDataResponseData
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.getProjects().let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let { projectList ->
                    _projects.clear()
                    _projects.addAll(projectList.toProjectListItem(masterData))
                }
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }
}