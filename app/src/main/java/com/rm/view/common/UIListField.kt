package com.rm.view.common

import com.rm.R
import kotlinx.coroutines.flow.MutableStateFlow

data class UIListField(
    val state: MutableStateFlow<MutableList<Any>> = MutableStateFlow(mutableListOf()),
    var hasError: Boolean = false,
    var showError: Boolean = false,
    var errorMessage: Int = R.string.empty,
)
