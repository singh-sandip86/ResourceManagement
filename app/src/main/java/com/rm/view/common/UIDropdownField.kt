package com.rm.view.common

import com.rm.R
import kotlinx.coroutines.flow.MutableStateFlow

data class UIDropdownField(
    val state: MutableStateFlow<Any> = MutableStateFlow(Any()),
    var hasError: Boolean = false,
    var showError: Boolean = false,
    var errorMessage: Int = R.string.empty,
)