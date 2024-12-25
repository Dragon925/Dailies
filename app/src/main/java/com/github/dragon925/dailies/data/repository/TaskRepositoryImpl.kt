package com.github.dragon925.dailies.data.repository

import android.util.Log
import com.github.dragon925.dailies.data.mapper.toDomain
import com.github.dragon925.dailies.data.mapper.toDto
import com.github.dragon925.dailies.data.model.DataSource
import com.github.dragon925.dailies.data.model.TaskDto
import com.github.dragon925.dailies.domain.model.Task
import com.github.dragon925.dailies.domain.model.TaskListState
import com.github.dragon925.dailies.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

class TaskRepositoryImpl(
    private val localSource: DataSource,
    date: LocalDate
) : TaskRepository {

    private val currentDate = MutableStateFlow(date)

    override fun changeDate(date: LocalDate) {
        currentDate.value = date
    }

    override fun loadTasks(): Flow<TaskListState> = localSource.getAllTasks().onEach { Log.d("LoadTasks", it.toString()) }.combine(
        currentDate
    ) { tasks, date ->
        val timeZone = TimeZone.currentSystemDefault()
        val currentDay = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val nextDay = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds()
        return@combine TaskListState(
            date = date,
            tasks = tasks.filter { it.dateStart * 1000 in currentDay..<nextDay }
                .sortedWith(
                    Comparator.comparing<TaskDto, Long> { it.dateStart }
                        .thenByDescending { it.dateEnd - it.dateStart }
                ).map { (it.toDomain(timeZone)) }
        )
    }

    override fun loadTask(id: Int): Flow<Task?> = localSource.getTaskById(id).map { it?.toDomain() }

    override suspend fun addTask(task: Task) {
        localSource.addTask(task.toDto())
    }

    override suspend fun deleteTask(id: Int) {
        localSource.deleteTaskById(id)
    }
}