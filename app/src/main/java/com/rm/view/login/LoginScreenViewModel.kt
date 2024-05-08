package com.rm.view.login

import com.rm.MainActivityViewModel
import com.rm.base.BaseViewModel
import com.rm.data.login.UILogin
import com.rm.network.ErrorResponse
import com.rm.preferences.SharedPrefsManager
import com.rm.utils.ErrorUtils
import com.rm.view.login.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    errorUtils: ErrorUtils
) : BaseViewModel(errorUtils) {
    val uiLogin = UILogin()

    private val _login: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val login = _login.asSharedFlow()

    fun login(
        mainActivityVM: MainActivityViewModel
    ) = launchWithViewModelScope(
        call = {
            mainActivityVM.showLoader()
            loginRepository.login(uiLogin.username.state.value, uiLogin.password.state.value)
                .let { response ->
                    if (response.isSuccessful) {
                        response.body()?.data?.let {
                            SharedPrefsManager.token = it.token
                            SharedPrefsManager.isUserLoggedIn = true
                            SharedPrefsManager.userId = it.id
                            SharedPrefsManager.userName = it.name
                            _login.emit(true)
                        }
                    } else {
                        handleError(response)
                        mainActivityVM.showErrorDialog()
                    }
                    mainActivityVM.hideLoader()
                }
        },
        exceptionCallback = { message ->
            handleError(ErrorResponse(message = message))
            mainActivityVM.showErrorDialog()
        }
    )

    fun combineAndValidate(): Flow<Boolean> {
        return combine(
            uiLogin.username.state,
            uiLogin.password.state
        ) { _, _ ->
            validate()
        }
    }

    private fun validate(): Boolean {
        uiLogin.validate()

        val hasError = uiLogin.hasError()
        uiLogin.enableLoginButton.update { hasError.not() }
        return hasError
    }
}