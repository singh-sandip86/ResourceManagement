package com.rm.view.template

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SpaceTop(top: Dp) {
    Spacer(modifier = Modifier.padding(PaddingValues(top = top)))
}

@Composable
fun Space(start: Dp) {
    Spacer(modifier = Modifier.padding(PaddingValues(start = start)))
}