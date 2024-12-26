@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.dragon925.dailies.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.github.dragon925.dailies.R
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TimePickerModal(
    onConfirm: (TimePickerState?) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) },
    ) {
        TimePicker(timePickerState,)
    }
}

@Composable
fun TimePickerModalInput(
    onConfirm: (TimePickerState?) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) },
    ) {
        TimeInput(timePickerState)
    }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        text = { content() }
    )
}

@Composable
fun TimePickerField(
    label: String,
    selectedTime: String,
    showError: Boolean = false,
    isPickerMode: Boolean = true,
    modifier: Modifier = Modifier,
    onTimeSelected: (TimePickerState?) -> Unit = {}
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = selectedTime,
        onValueChange = {},
        label = { Text(label) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(painterResource(R.drawable.ic_time_24) , contentDescription = null)
            }
        },
        readOnly = true,
        isError = selectedTime.isBlank() && showError,
        supportingText = {
            if (selectedTime.isBlank() && showError) {
                Text(stringResource(R.string.error_empty_time), Modifier.clearAndSetSemantics {  })
            }
        },
        shape = RoundedCornerShape(12.dp)
    )

    if (showPicker) {
        if (isPickerMode)
            TimePickerModal(onConfirm = onTimeSelected) { showPicker = false }
        else
            TimePickerModalInput(onConfirm = onTimeSelected) { showPicker = false }
    }
}
