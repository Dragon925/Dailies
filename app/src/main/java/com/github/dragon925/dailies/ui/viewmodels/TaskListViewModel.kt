package com.github.dragon925.dailies.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.dragon925.dailies.domain.model.TaskListState
import com.github.dragon925.dailies.domain.repository.TaskRepository
import com.github.dragon925.dailies.ui.mapper.toListUIState
import com.github.dragon925.dailies.ui.models.TaskListUIEvent
import com.github.dragon925.dailies.ui.models.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val loading = MutableStateFlow(true)
    private val data = MutableStateFlow<TaskListState?>(null)

    val state = loading.combine(data) { isLoading, tasks ->
        UIState(isLoading, state = tasks?.toListUIState())
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadTasks().onEach { loading.value = false }.collect { state ->
                data.value = state
            }
        }
    }

    fun consume(event: TaskListUIEvent) {
        when(event) {
            is TaskListUIEvent.LoadTasks -> loadTasks(event.date)
            is TaskListUIEvent.DeleteTask -> deleteTask(event.taskId)
        }
    }

    private fun deleteTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(id)
        }
    }

    private fun loadTasks(date: LocalDate) {
        if (loading.value && date == data.value?.date) return

        loading.value = true
        repository.changeDate(date)
    }
}