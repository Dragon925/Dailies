package com.github.dragon925.dailies.ui.mapper

import com.github.dragon925.dailies.domain.model.Task
import com.github.dragon925.dailies.domain.model.TaskListState
import com.github.dragon925.dailies.ui.models.TaskInfoUIState
import com.github.dragon925.dailies.ui.models.TaskItem
import com.github.dragon925.dailies.ui.models.TaskListUIState
import com.github.dragon925.dailies.ui.models.TimeRow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant

private val RUSSIAN_ABBREVIATED: MonthNames = MonthNames(
    listOf("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
)

val dateFormat = LocalDate.Format {
    dayOfMonth(); char(' '); monthName(RUSSIAN_ABBREVIATED); char(' '); year()
}

val timeFormat = LocalTime.Format {
    hour(); char(':'); minute()
}

val dateTimeFormat = LocalDateTime.Format {
    dayOfMonth(); char('.'); monthNumber(); char('.'); year(); char(' '); hour(); char(':'); minute()
}

fun TaskListState.toListUIState(): TaskListUIState {
    val timeZone = TimeZone.currentSystemDefault()
    val row = mutableListOf<TimeRow>()
    val hourGroups = tasks.groupBy { it.dateStart.hour }
    for (hour in 0..<24) {
        row.add(
            TimeRow(
                time = "$hour:00 – ${hour + 1}:00",
                tasks = hourGroups[hour]?.map { it.toItem(timeZone) } ?: emptyList()
            )
        )
    }
    return TaskListUIState(
        date.format(dateFormat),
        row
    )
}

fun Task.toItem(timeZone: TimeZone = TimeZone.currentSystemDefault()): TaskItem {
    val start = dateStart.toInstant(timeZone)
    val end = dateEnd.toInstant(timeZone)
    return TaskItem(
        id,
        name,
        dateStart.time.format(timeFormat),
        dateEnd.time.format(timeFormat),
        start.periodUntil(end, timeZone).hours
    )
}

fun Task.toInfoUIState() = TaskInfoUIState(
    id,
    name,
    dateStart.format(dateTimeFormat),
    dateEnd.format(dateTimeFormat),
    description
)