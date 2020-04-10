package com.example.coronamap

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class CoronaApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        configureRealm()
    }

    private fun configureRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }
}