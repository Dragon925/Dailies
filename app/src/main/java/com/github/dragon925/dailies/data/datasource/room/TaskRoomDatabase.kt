package com.github.dragon925.dailies.data.datasource.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.dragon925.dailies.data.datasource.room.dao.TaskDao
import com.github.dragon925.dailies.data.datasource.room.entity.TaskEntity
import com.github.dragon925.dailies.data.mapper.toDto
import com.github.dragon925.dailies.data.mapper.toEntity
import com.github.dragon925.dailies.data.model.DataSource
import com.github.dragon925.dailies.data.model.TaskDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}

class TaskDatabase(private val database: TaskRoomDatabase) {

    private val dao get() = database.taskDao()

    val databaseSource: DataSource get() = object : DataSource {
        override fun getAllTasks(): Flow<List<TaskDto>> = dao.getAll().map { tasks ->
            tasks.map { it.toDto() }
        }

        override fun getTaskById(id: Int): Flow<TaskDto?> = dao.getTaskById(id)
            .map { it?.toDto() }
            .catch { emit(null) }

        override suspend fun addTask(task: TaskDto) {
            dao.addTask(task.toEntity())
        }

        override suspend fun deleteTaskById(id: Int) {
            dao.deleteTaskById(id)
        }
    }
}

fun TaskDatabase(applicationContext: Context): TaskDatabase {
    val taskDatabase = Room.databaseBuilder(
        checkNotNull(applicationContext.applicationContext),
        TaskRoomDatabase::class.java,
        "tasks_database"
    ).build()
    return TaskDatabase(taskDatabase)
}

