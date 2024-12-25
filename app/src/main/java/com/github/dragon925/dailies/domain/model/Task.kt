package com.github.dragon925.dailies.domain.model

import kotlinx.datetime.LocalDateTime

data class Task(
    val id: Int,
    val name: String,
    val dateStart: LocalDateTime,
    val dateEnd: LocalDateTime,
    val description: String
)
