package com.rm.data.project

import com.rm.R
import com.rm.view.common.UIDropdownField
import com.rm.view.common.UIListField
import com.rm.view.common.UITextField
import kotlinx.coroutines.flow.MutableStateFlow

data class UIAddProject(
    val projectId: UITextField = UITextField(),
    val projectName: UITextField = UITextField(),
    val projectClient: UIDropdownField = UIDropdownField(),
    val projectDescription: UITextField = UITextField(),
    val projectState: UIDropdownField = UIDropdownField(),
    val projectScope: UIListField = UIListField(),
    val projectStartDate: UITextField = UITextField(),
    val projectEndDate: UITextField = UITextField(),
    val projectResourceTypeList: UIListField = UIListField(),
    val projectSubProjectList: MutableList<UISubProject> = mutableListOf(),

//    val resourceDesignation: UIDropdownField = UIDropdownField(),
//    val resourceEmail: UITextField = UITextField(),
//    val resourceContactNumber: UITextField = UITextField(),
//
//    val resourcePassword: UITextField = UITextField(),
    var enableSubmitButton: MutableStateFlow<Boolean> = MutableStateFlow(false)
) {
    fun validateProject() {
        projectName.apply {
            val name = state.value
            val nameLength = name.length

            hasError = name.isEmpty() || nameLength < 4
            showError = name.isNotEmpty() && hasError
            errorMessage = if (hasError) {
                R.string.project_name_length_error
            } else R.string.empty
        }

        projectDescription.apply {
            val desc = state.value
            val descLength = desc.length

            hasError = desc.isEmpty() || descLength < 20
            showError = desc.isNotEmpty() && hasError
            errorMessage = if (hasError) {
                R.string.project_description_length_error
            } else R.string.empty
        }

//        projectScope.apply {
//            val scope = state.value
//            val scopeListSize = scope.size
//
//            hasError = scopeListSize == 0
//            showError = hasError
//            errorMessage = R.string.scope_validation_message
//        }
    }

    fun hasError(): Boolean {
        return projectName.hasError || projectDescription.hasError
    }
}