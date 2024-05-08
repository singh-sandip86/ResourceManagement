package com.rm.base

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.network.ErrorResponse
import com.rm.network.Event
import com.rm.utils.ErrorUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

abstract class BaseViewModel(internal val errorUtils: ErrorUtils) : ViewModel() {

    fun launchWithViewModelScope(call: suspend () -> Unit) {
        launchWithViewModelScope(call) {
            Log.d("Coroutine", "Coroutine Exception $it")
        }
    }

    fun launchWithViewModelScope(
        call: suspend () -> Unit,
        exceptionCallback: (exception: String) -> Unit
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, exception ->
            exceptionCallback(exception.localizedMessage ?: "Something went wrong")
        }) {
            call.invoke()
        }
    }

    /** Try to fetch a viewModel in [store] */
    @Composable
    inline fun <reified T : ViewModel, S : ViewModelStoreOwner> viewModelInStore(store: S): Result<T> =
        runCatching {
            var result: Result<T>? = null
            CompositionLocalProvider(LocalViewModelStoreOwner provides store) {
                result = runCatching { viewModel(T::class.java) }
            }
            result!!.getOrThrow()
        }

    /** Try to fetch a viewModel with current context (i.e. activity)  */
    @Composable
    inline fun <reified T : ViewModel> safeActivityViewModel(): Result<T> = runCatching {
        val activity = LocalContext.current as? ViewModelStoreOwner
            ?: throw IllegalStateException("Current context is not a viewModelStoreOwner.")
        return viewModelInStore(activity)
    }

    /** Force fetch a viewModel inside context's viewModelStore */
    @Composable
    inline fun <reified T : ViewModel> activityViewModel(): T = safeActivityViewModel<T>().getOrThrow()


    /* Error messages from API calls */
    var error: Event<ErrorResponse>? = null

    /**
     *  Parse the error from API response to our model ErrorResponse
     *  Post it to LiveData for observers to check and handle errors on UI side
     */
    fun handleError(response: Response<*>): ErrorResponse {
        val errorValue = errorUtils.parseError(response)
        return handleError(errorValue)
    }

    /*
    * If error is parsed already then we can not parse it again as "errorBody()" will return null
    * In that cases we need to use below function and send the previously parsed result
    * */
    fun handleError(errorResponse: ErrorResponse): ErrorResponse {
        setError(errorResponse)
        return errorResponse
    }

    private fun setError(errorResponse: ErrorResponse) {
        error = Event(errorResponse)
    }
}