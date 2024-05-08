package com.rm.data.resource

import com.rm.R
import com.rm.view.common.UIDropdownField
import com.rm.view.common.UIListField
import com.rm.view.common.UITextField
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.regex.Pattern

data class UIAddResource(
    val resourceId: UITextField = UITextField(),
    val resourceName: UITextField = UITextField(),
    val resourceType: UIDropdownField = UIDropdownField(),
    val resourceDesignation: UIDropdownField = UIDropdownField(),
    val resourceEmail: UITextField = UITextField(),
    val resourceContactNumber: UITextField = UITextField(),
    val resourceTechnology: UIListField = UIListField(),
    val dateOfJoining: UITextField = UITextField(),
    val resourcePassword: UITextField = UITextField(),
    val linkedInProfile: UITextField = UITextField(),
    val compensation: UITextField = UITextField(),
    var enableSubmitButton: MutableStateFlow<Boolean> = MutableStateFlow(false)
) {

    fun validate(editEnabled: Boolean) {

        resourceName.apply {
            val name = state.value
            val nameLength = name.length

            hasError = name.isEmpty() || nameLength < 4
            showError = name.isNotEmpty() && hasError
            errorMessage = if (nameLength < 4) {
                R.string.username_length_error
            } else R.string.empty
        }

        resourceEmail.apply {
            val email = state.value
            val regex =
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

            hasError = email.isEmpty() || Pattern.compile(regex).matcher(email).matches().not()
            showError = email.isNotEmpty() && hasError
            errorMessage = if (email.isEmpty())
                R.string.email_validation_empty_message
            else R.string.email_validation_message
        }

        resourceContactNumber.apply {
            val number = state.value
            val numberLength = number.length
            val regex = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$"

            hasError =
                number.isEmpty() || numberLength < 10 || numberLength > 14 || Pattern.compile(regex)
                    .matcher(number).matches().not()
            showError = number.isNotEmpty() && hasError
            errorMessage = if (number.isEmpty())
                R.string.contact_number_validation_empty_message
            else R.string.contact_number_validation_message
        }

        resourceTechnology.apply {
            val technology = state.value
            val technologyListSize = technology.size

            hasError = technologyListSize == 0
            showError = hasError
            errorMessage = R.string.technology_validation_message
        }

        dateOfJoining.apply {
            val doj = state.value
            val isEmpty = if (editEnabled) true else doj.isEmpty()

            hasError = isEmpty
            showError = doj.isNotEmpty() && hasError
            errorMessage = R.string.date_of_joining_validation_empty_message
        }

        resourcePassword.apply {
            val password = state.value
            val passwordLength = password.length

            val isEmpty = if (editEnabled) true else password.isEmpty()
            val lengthValidate = if (editEnabled && password.isEmpty()) true else passwordLength < 6

            hasError = isEmpty || lengthValidate
            showError = password.isNotEmpty() && hasError
            errorMessage = if (password.isEmpty())
                R.string.password_validation_empty_message
            else R.string.password_validation_message
        }

        linkedInProfile.apply {
            val profile = state.value
            val isEmpty = if (editEnabled) true else profile.isEmpty()

            hasError = isEmpty
            showError = profile.isNotEmpty() && hasError
            errorMessage = R.string.linked_in_validation_empty_message
        }

        compensation.apply {
            val amount = state.value
            val isEmpty = if (editEnabled) true else amount.isEmpty()

            hasError = isEmpty
            showError = amount.isNotEmpty() && hasError
            errorMessage = R.string.linked_in_validation_empty_message
        }
    }

    fun hasError(): Boolean {
        return resourceName.hasError || resourceType.hasError
    }
}