@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.dragon925.dailies.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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


@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    showError: Boolean = false,
    mode: DisplayMode = DisplayMode.Picker,
    modifier: Modifier = Modifier,
    onDateSelected: (Long?) -> Unit = {}
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {showPicker = true}) {
                Icon( painterResource(R.drawable.ic_event_24), contentDescription = null )
            }
        },
        readOnly = true,
        isError = selectedDate.isBlank() && showError,
        supportingText = {
            if (selectedDate.isBlank() && showError) {
                Text(stringResource(R.string.error_empty_date), Modifier.clearAndSetSemantics {  })
            }
        },
        shape = RoundedCornerShape(12.dp)
    )

    if (showPicker) {
        if (mode == DisplayMode.Picker)
            DatePickerModal(onDateSelected = onDateSelected) { showPicker = false }
        else
            DatePickerModalInput(onDateSelected = onDateSelected) { showPicker = false }
    }
}