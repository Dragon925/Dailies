package com.github.dragon925.dailies.domain.repository

import com.github.dragon925.dailies.domain.model.Task
import com.github.dragon925.dailies.domain.model.TaskListState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TaskRepository {

    fun changeDate(date: LocalDate)

    fun loadTasks(): Flow<TaskListState>

    fun loadTask(id: Int): Flow<Task?>

    suspend fun addTask(task: Task)

    suspend fun deleteTask(id: Int)
}