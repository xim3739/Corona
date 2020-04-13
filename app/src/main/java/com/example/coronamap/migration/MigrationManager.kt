package com.example.coronamap.migration

import com.example.coronamap.preference.CoronaPreference
import io.realm.*

enum class MigrationVersion {
    Initial { //0
        override fun migrate(dynamicRealm: DynamicRealm) {}
    },
    addCityName { //1
        override fun migrate(dynamicRealm: DynamicRealm) {
//            val schema: RealmSchema = dynamicRealm.schema
//            val mCoronaMapSchema: RealmObjectSchema? = schema.get("CoronaMapModel")
//            mCoronaMapSchema?.addField("cityName", String::class.java)
        }
    },
    ChangeLocationType { //2
        override fun migrate(dynamicRealm: DynamicRealm) {
            val schema: RealmSchema = dynamicRealm.schema
            val mCoronaMapSchema: RealmObjectSchema? = schema.get("CoronaMapModel")
            mCoronaMapSchema?.addField("locationX_tmp", Double::class.java)
            mCoronaMapSchema?.addField("locationY_tmp", Double::class.java)

            mCoronaMapSchema?.transform{obj: DynamicRealmObject ->
                val locationX: String = obj.get("locationX")
                val locationY: String = obj.get("locationY")

                obj.setDouble("locationX_tmp", locationX.toDouble())
                obj.setDouble("locationY_tmp", locationY.toDouble())
            }

            mCoronaMapSchema?.removeField("locationX")
            mCoronaMapSchema?.removeField("locationY")

            mCoronaMapSchema?.renameField("locationX_tmp", "locationX")
            mCoronaMapSchema?.renameField("locationY_tmp", "locationY")
        }
    },
    Last { //3
        override fun migrate(dynamicRealm: DynamicRealm) {}
    };

    abstract fun migrate(dynamicRealm: DynamicRealm)
}

object MigrationManager {
    fun migration() {
        val targetVersion = MigrationVersion.Last.ordinal.toLong()
        var currentVersion = CoronaPreference.realmSchemeVersion

        val config= RealmConfiguration.Builder()
                .schemaVersion(targetVersion)
                .migration { realm, oldVersion, newVersion ->
                    // oldVersion = 1, newVersion = 2
                    while (currentVersion < targetVersion) {
                        MigrationVersion.values()[currentVersion.toInt()].migrate(realm) //
                        currentVersion += 1
                    }
                }
                .build()
        CoronaPreference.realmSchemeVersion = currentVersion
        Realm.setDefaultConfiguration(config)
    }
}