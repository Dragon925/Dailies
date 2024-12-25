package com.github.dragon925.dailies.data.mapper

import com.github.dragon925.dailies.data.model.TaskDto
import com.github.dragon925.dailies.domain.model.Task
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun TaskDto.toDomain(timeZone: TimeZone = TimeZone.currentSystemDefault()) = Task(
    id,
    name,
    dateStart = Instant.fromEpochMilliseconds(dateStart * 1000).toLocalDateTime(timeZone),
    dateEnd =  Instant.fromEpochMilliseconds(dateEnd * 1000).toLocalDateTime(timeZone),
    description
)

fun Task.toDto(timeZone: TimeZone = TimeZone.currentSystemDefault()) = TaskDto(
    id,
    dateStart = dateStart.toInstant(timeZone).toEpochMilliseconds() / 1000,
    dateEnd = dateEnd.toInstant(timeZone).toEpochMilliseconds() / 1000,
    name,
    description
)