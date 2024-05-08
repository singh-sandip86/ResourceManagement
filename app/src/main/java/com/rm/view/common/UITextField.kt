package com.rm.view.common

import com.rm.R
import com.rm.utils.empty
import kotlinx.coroutines.flow.MutableStateFlow

data class UITextField(
    val state: MutableStateFlow<String> = MutableStateFlow(String.empty()),
    var hasError: Boolean = false,
    var showError: Boolean = false,
    var errorMessage: Int = R.string.empty,
)