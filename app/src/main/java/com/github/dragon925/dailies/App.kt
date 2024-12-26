package com.github.dragon925.dailies

import android.app.Application
import com.github.dragon925.dailies.data.datasource.room.TaskDatabase

class App : Application() {

    val database: TaskDatabase by lazy {
        TaskDatabase(this)
    }
}