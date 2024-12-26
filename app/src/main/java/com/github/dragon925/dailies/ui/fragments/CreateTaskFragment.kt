@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.dragon925.dailies.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.github.dragon925.dailies.R
import com.github.dragon925.dailies.domain.model.Task
import com.github.dragon925.dailies.ui.activity.ui.theme.DailiesTheme
import com.github.dragon925.dailies.ui.components.DatePickerField
import com.github.dragon925.dailies.ui.components.TimePickerField
import com.github.dragon925.dailies.ui.mapper.dateFormat
import com.github.dragon925.dailies.ui.mapper.timeFormat
import com.github.dragon925.dailies.ui.mapper.timestampToLocalDateTime
import com.github.dragon925.dailies.ui.viewmodels.TaskCreateViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format

class CreateTaskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DailiesTheme {
                    CreateTaskForm()
                }
            }
        }
    }

    @Composable
    fun CreateTaskForm(viewModel: TaskCreateViewModel = viewModel(
        factory = TaskCreateViewModel.Factory
    )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = { Text(stringResource(R.string.create_new_task)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                findNavController().navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Form(Modifier.padding(innerPadding)) { task ->
                viewModel.saveTask(task)
                findNavController().navigateUp()
            }
        }
    }
}

@Composable
fun Form(
    modifier: Modifier = Modifier,
    onSave: (Task) -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dayStart by remember { mutableStateOf<LocalDate?>(null) }
    var timeStart by remember { mutableStateOf<LocalTime?>(null) }
    var dayEnd by remember { mutableStateOf<LocalDate?>(null) }
    var timeEnd by remember { mutableStateOf<LocalTime?>(null) }

    var check by remember { mutableStateOf(false) }

    val timeZone = TimeZone.currentSystemDefault()

    Column(
        modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(min = 56.dp),
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name)) },
            singleLine = true,
            trailingIcon = { if (name.isNotEmpty()) {
                IconButton(onClick = {name = ""}) {
                    Icon( Icons.Outlined.Clear, contentDescription = null )
                } }
            },
            isError = check && name.isBlank(),
            supportingText = {
                if (check && name.isBlank()) {
                    Text(stringResource(R.string.error_empty_name), Modifier.clearAndSetSemantics {  })
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        Text(
            stringResource(R.string.begin),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {

            DatePickerField(
                label = stringResource(R.string.day),
                selectedDate = dayStart?.format(dateFormat) ?: "",
                showError = check,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 8.dp)
                    .heightIn(min = 56.dp)
            ) { timestamp ->
                dayStart = timestamp?.let { timestampToLocalDateTime(it, timeZone).date }
            }

            TimePickerField(
                label = stringResource(R.string.time),
                selectedTime = timeStart?.format(timeFormat) ?: "",
                showError = check,
                modifier = Modifier
                    .weight(0.4f)
                    .padding(horizontal = 8.dp)
                    .heightIn(min = 56.dp)
            ) { state ->
                timeStart = state?.let { LocalTime(it.hour, it.minute) }
            }
        }

        Text(
            stringResource(R.string.end),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            DatePickerField(
                label = stringResource(R.string.day),
                selectedDate = dayEnd?.format(dateFormat)?: "",
                showError = check,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 8.dp)
                    .heightIn(min = 56.dp)
            ) {
                dayEnd = it?.let { timestampToLocalDateTime(it, timeZone).date }
            }

            TimePickerField(
                label = stringResource(R.string.time),
                selectedTime = timeEnd?.format(timeFormat) ?: "",
                showError = check,
                modifier = Modifier
                    .weight(0.4f)
                    .padding(horizontal = 8.dp)
                    .heightIn(min = 56.dp)
            ) { state ->
                timeEnd = state?.let { LocalTime(it.hour, it.minute) }
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .heightIn(min = 56.dp),
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description)) },
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                check = true
                if (name.isNotBlank() && dayStart != null && dayEnd != null && timeStart != null &&timeEnd != null) {
                    onSave(
                        Task(
                            0,
                            name,
                            dateStart = dayStart!!.atTime(timeStart!!),
                            dateEnd = dayEnd!!.atTime(timeEnd!!),
                            description
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(min = 56.dp)
        ) {
            Text(stringResource(R.string.save))
        }

    }
}


@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun FormPreview() {
    DailiesTheme {
        Form()
    }
}