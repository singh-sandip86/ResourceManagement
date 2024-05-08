package com.rm.view.resource.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.rm.MainActivityViewModel
import com.rm.api.response.masterdata.MasterDataResponseData
import com.rm.api.response.resource.toResourceItemList
import com.rm.base.BaseViewModel
import com.rm.data.resource.ResourceItemList
import com.rm.utils.ErrorUtils
import com.rm.utils.empty
import com.rm.view.resource.repository.ResourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ResourceDetailsViewModel @Inject constructor(
    private val repository: ResourceRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {

    var selectedResourceId = String.empty()

    private var _resourceDetail = mutableStateOf(ResourceItemList())
    val resourceDetail: MutableState<ResourceItemList>
        get() = _resourceDetail

    fun getResourceDetail(
        masterData: MasterDataResponseData,
        mainViewModel: MainActivityViewModel
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.getResourceDetail(resourceId = selectedResourceId).let { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let { resourceDetail ->
                    _resourceDetail.value = resourceDetail.resource.toResourceItemList(masterData = masterData)
                }
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }

    private val _deleteResourceResponse = MutableStateFlow(false)
    val deleteResourceResponse = _deleteResourceResponse.asSharedFlow()

    fun deleteResource(
        resourceId: String,
        mainViewModel: MainActivityViewModel
    ) = launchWithViewModelScope {
        mainViewModel.showLoader()
        repository.deleteResource(resourceId = resourceId).let { response ->
            if (response.isSuccessful) {
                _deleteResourceResponse.emit(true)
            } else {
                handleError(response)
                mainViewModel.showErrorDialog()
            }
            mainViewModel.hideLoader()
        }
    }
}