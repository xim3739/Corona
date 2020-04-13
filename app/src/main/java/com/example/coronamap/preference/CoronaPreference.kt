package com.example.coronamap.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.coronamap.CoronaApplication

object CoronaPreference {
    val preferenceName = "CoronaPreference"
    private val sharedPreferences: SharedPreferences
        get() = CoronaApplication.shared.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    var realmSchemeVersion: Long
        get() = sharedPreferences.getLong("realmSchemeVersion", 0)
        set(value) = sharedPreferences.edit().putLong("realmSchemeVersion", value).apply()
}