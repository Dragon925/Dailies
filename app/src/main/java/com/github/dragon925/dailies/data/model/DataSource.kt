package com.github.dragon925.dailies.data.model

import kotlinx.coroutines.flow.Flow

interface DataSource {

    fun getAllTasks(): Flow<List<TaskDto>>

    fun getTaskById(id: Int): Flow<TaskDto?>

    suspend fun addTask(task: TaskDto)

    suspend fun deleteTaskById(id: Int)
}