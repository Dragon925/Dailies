package com.github.dragon925.dailies.ui.models

import kotlinx.datetime.LocalDate

sealed class TaskListUIEvent {

    data class LoadTasks(val date: LocalDate) : TaskListUIEvent()

    data class DeleteTask(val taskId: Int) : TaskListUIEvent()

}