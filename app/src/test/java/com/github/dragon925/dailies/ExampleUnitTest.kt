package com.github.dragon925.dailies

import com.github.dragon925.dailies.data.datasource.json.TasksJSONSource
import com.github.dragon925.dailies.data.mapper.toDomain
import com.github.dragon925.dailies.data.model.TaskDto
import com.github.dragon925.dailies.data.repository.TaskRepositoryImpl
import com.github.dragon925.dailies.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private lateinit var repositoryJson: TaskRepository

    @Before
    fun setup() {
        repositoryJson = TaskRepositoryImpl(TasksJSONSource, LocalDate(2024, 12, 26))
    }

    @Test
    fun testGetTaskFromJsonById() {
        val expected = TaskDto(
            7,
        1733178000,
        1733192400,
        "Событие 7",
        "Описание события 7"
        ).toDomain(TimeZone.currentSystemDefault())
        runBlocking {
            assertEquals(expected, repositoryJson.loadTask(7).first())
        }
    }
}