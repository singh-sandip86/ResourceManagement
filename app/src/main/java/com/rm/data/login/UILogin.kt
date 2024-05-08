package com.rm.data.login

import com.rm.R
import com.rm.view.common.UITextField
import kotlinx.coroutines.flow.MutableStateFlow

data class UILogin(
    val username: UITextField = UITextField(),
    val password: UITextField = UITextField(),
    var enableLoginButton: MutableStateFlow<Boolean> = MutableStateFlow(false)
) {
    fun validate() {

        username.apply {
            val login = state.value
            val loginLength = login.length

            hasError = login.isEmpty() || loginLength < 4
            showError = login.isNotEmpty() && hasError
            errorMessage = if (loginLength < 4) {
                R.string.username_length_error
            } else R.string.empty
        }

        password.apply {
            val pwd = state.value
            val pwdLength = pwd.length

            hasError = pwd.isEmpty() || pwdLength < 4
            showError = pwd.isNotEmpty() && hasError
            errorMessage = if (pwdLength < 4) {
                R.string.password_length_error
            } else R.string.empty
        }
    }

    fun hasError(): Boolean {
        return username.hasError || password.hasError
    }
}