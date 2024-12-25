package com.github.dragon925.dailies.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dragon925.dailies.domain.model.Task
import com.github.dragon925.dailies.domain.repository.TaskRepository
import com.github.dragon925.dailies.ui.mapper.toInfoUIState
import com.github.dragon925.dailies.ui.models.TaskInfoUIEvent
import com.github.dragon925.dailies.ui.models.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TaskInfoViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val loading = MutableStateFlow(false)
    private val data = MutableStateFlow<Task?>(null)

    val state = loading.combine(data) { isLoading, task ->
        UIState(isLoading, state = task?.toInfoUIState())
    }

    fun consume(event: TaskInfoUIEvent) {
        when(event) {
            is TaskInfoUIEvent.DeleteTask -> deleteTask(event.taskId)
            is TaskInfoUIEvent.LoadTask -> loadTask(event.taskId)
        }
    }

    private fun deleteTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(id)
        }
    }

    private fun loadTask(id: Int) {
        if (loading.value) return

        viewModelScope.launch(Dispatchers.IO) {
            repository.loadTask(id).onStart {
                loading.value = true
            }.onEach { loading.value = false }.collect { task ->
                data.value = task
            }
        }
    }
}