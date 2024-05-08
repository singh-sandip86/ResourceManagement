package com.rm.view.template

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDateValidator(
    initialDateInMillis: Long,
    onDismissRequest: () -> Unit,
    onConfirmClick: (selectedDate: Long) -> Unit,
    onDismissClick: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateInMillis)
    val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
    DatePickerDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onConfirmClick(it) }
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissClick()
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DatePicker(
                state = datePickerState,
                // Blocks Sunday and Saturday from being selected.
                dateValidator = { utcDateInMills ->
                    val dayOfWeek = Instant.ofEpochMilli(utcDateInMills).atZone(ZoneId.of("UTC"))
                        .toLocalDate().dayOfWeek
                    dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.SATURDAY
                }
            )
        }
    }
}