package com.example.coronamap

import android.app.Application
import com.example.coronamap.migration.MigrationManager
import io.realm.*

class CoronaApplication: Application() {
    companion object {
        lateinit var shared: CoronaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        shared = this
        Realm.init(this)
        MigrationManager.migration()
    }
}