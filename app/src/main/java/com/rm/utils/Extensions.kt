package com.rm.utils

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun String.Companion.empty() = ""

fun Bundle.getStringOrThrowException(key: String): String {
    val value = getString(key)
    requireNotNull(value) { "$key parameter wasn't found. Please make sure it's set!" }
    return value
}

@Composable
fun <T> getMutableStateValue(state: MutableStateFlow<T>): T {
    return state.collectAsStateWithLifecycle().value
}

fun <T> setMutableStateValue(state: MutableStateFlow<T>, value: T) {
    state.value = value
}

fun Long.millisToString(): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(this)
}

fun Date.dateToString(): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(this)
}

fun String.stringToMillis(): Long {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    return LocalDate.parse(this, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}