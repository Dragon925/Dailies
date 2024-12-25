package com.github.dragon925.dailies.domain.model

import kotlinx.datetime.LocalDate

data class TaskListState(
    val date: LocalDate,
    val tasks: List<Task>
)
