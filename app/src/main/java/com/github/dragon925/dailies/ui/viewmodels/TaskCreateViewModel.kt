package com.github.dragon925.dailies.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.dragon925.dailies.App
import com.github.dragon925.dailies.data.repository.TaskRepositoryImpl
import com.github.dragon925.dailies.domain.model.Task
import com.github.dragon925.dailies.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TaskCreateViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    fun saveTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val repository = TaskRepositoryImpl(
                    localSource = (this[APPLICATION_KEY] as App).database.databaseSource,
                    today
                )
                TaskCreateViewModel(repository)
            }
        }
    }
}