package com.github.dragon925.dailies.data.datasource.json

import com.github.dragon925.dailies.data.model.DataSource
import com.github.dragon925.dailies.data.model.TaskDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object TasksJSONSource : DataSource {

    private const val SAMPLE_DATA: String = """
[
    {
        "id": 1,
        "date_start": 1733025600,
        "date_finish": 1733029200,
        "name": "Событие 1",
        "description": "Описание события 1"
    },
    {
        "id": 2,
        "date_start": 1733032800,
        "date_finish": 1733036400,
        "name": "Событие 2",
        "description": "Описание события 2"
    },
    {
        "id": 3,
        "date_start": 1733040000,
        "date_finish": 1733061600,
        "name": "Событие 3",
        "description": "Описание события 3"
    },
    {
        "id": 4,
        "date_start": 1733078400,
        "date_finish": 1733100000,
        "name": "Событие 4",
        "description": "Описание события 4"
    },
    {
        "id": 5,
        "date_start": 1733116800,
        "date_finish": 1733143200,
        "name": "Событие 5",
        "description": "Описание события 5"
    },
    {
        "id": 6,
        "date_start": 1733161200,
        "date_finish": 1733169600,
        "name": "Событие 6",
        "description": "Описание события 6"
    },
    {
        "id": 7,
        "date_start": 1733178000,
        "date_finish": 1733192400,
        "name": "Событие 7",
        "description": "Описание события 7"
    },
    {
        "id": 8,
        "date_start": 1733214000,
        "date_finish": 1733228400,
        "name": "Событие 8",
        "description": "Описание события 8"
    },
    {
        "id": 9,
        "date_start": 1733234400,
        "date_finish": 1733241600,
        "name": "Событие 9",
        "description": "Описание события 9"
    },
    {
        "id": 10,
        "date_start": 1733250000,
        "date_finish": 1733260800,
        "name": "Событие 10",
        "description": "Описание события 10"
    }
]
"""

    private var cache: String = SAMPLE_DATA

    private val tasks = MutableStateFlow(
        Json.decodeFromString<List<TaskDto>>(cache)
    )

    override fun getAllTasks(): Flow<List<TaskDto>> = tasks.asStateFlow().onEach {
        val json = Json.encodeToString(it)
        withContext(Dispatchers.Main) {
            cache = json
        }
    }

    override fun getTaskById(id: Int): Flow<TaskDto?> = tasks.map { tasks ->
        tasks.find { it.id == id }
    }

    override suspend fun addTask(task: TaskDto) {
        val list = tasks.value.toMutableList()
        val newId = list.maxOf { it.id } + 1
        list.add(task.copy(id = newId))
        tasks.value = list
    }

    override suspend fun deleteTaskById(id: Int) {
        tasks.update { list -> list.filterNot { it.id == id } }
    }
}
