package com.rm

import androidx.compose.runtime.mutableStateOf
import com.rm.api.response.masterdata.*
import com.rm.api.response.project.toProjectList
import com.rm.base.BaseViewModel
import com.rm.data.project.ProjectNameList
import com.rm.utils.ErrorUtils
import com.rm.view.login.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {

    var showErrorDialog  = mutableStateOf(false)

    fun showErrorDialog() { showErrorDialog.value = true }

    fun hideErrorDialog() { showErrorDialog.value = false }

    val showLoader = mutableStateOf(false)

    fun showLoader() { showLoader.value = true }

    fun hideLoader() { showLoader.value = false }

    fun isLoaderStopped(): Boolean { return showLoader.value.not() }

    val launchLoginScreen = mutableStateOf(false)

    fun launchLoginScreen() { launchLoginScreen.value = true }

    fun setLaunchLoginScreenFalse() { launchLoginScreen.value = false }

    var masterData: MasterDataResponseData? = null

    fun getMasterData() = launchWithViewModelScope {
        loginRepository.getMasterData().let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    masterData = it
                    getProjectNames()
                }
            } else {
                handleError(response)
                showErrorDialog()
            }
        }
    }

    var projectName: ProjectNameList? = null
    val projectNameSuccess: MutableSharedFlow<Boolean> = MutableSharedFlow()

    private fun getProjectNames() = launchWithViewModelScope {
        loginRepository.getProjectName().let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    projectName = it.toProjectList()
                    projectNameSuccess.emit(true)
                }
            } else {
                handleError(response)
                showErrorDialog()
            }
        }
    }
}