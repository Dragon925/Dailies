package com.github.dragon925.dailies.ui.models

sealed class TaskInfoUIEvent {

    data class LoadTask(val taskId: Int): TaskInfoUIEvent()

    data class DeleteTask(val taskId: Int): TaskInfoUIEvent()
}
