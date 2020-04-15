package com.example.coronamap.migration

import com.example.coronamap.model.CoronaLocationModel
import com.example.coronamap.preference.CoronaPreference
import io.realm.*

enum class MigrationVersion {
    Initial { //0
        override fun migrate(dynamicRealm: DynamicRealm) {}
    },
    addCityName { //1 add cityName Field
        override fun migrate(dynamicRealm: DynamicRealm) {
//            val schema: RealmSchema = dynamicRealm.schema
//            val mCoronaMapSchema: RealmObjectSchema? = schema.get("CoronaMapModel")
//            mCoronaMapSchema?.addField("cityName", String::class.java)
        }
    },
    changeLocationType { //2 change data type
        override fun migrate(dynamicRealm: DynamicRealm) {
//            val schema: RealmSchema = dynamicRealm.schema
//            val mCoronaMapSchema: RealmObjectSchema? = schema.get("CoronaMapModel")
//            mCoronaMapSchema?.addField("locationX_tmp", Double::class.java)
//            mCoronaMapSchema?.addField("locationY_tmp", Double::class.java)
//
//            mCoronaMapSchema?.transform{obj: DynamicRealmObject ->
//                val locationX: String = obj.get("locationX")
//                val locationY: String = obj.get("locationY")
//
//                obj.setDouble("locationX_tmp", locationX.toDouble())
//                obj.setDouble("locationY_tmp", locationY.toDouble())
//            }
//
//            mCoronaMapSchema?.removeField("locationX")
//            mCoronaMapSchema?.removeField("locationY")
//
//            mCoronaMapSchema?.renameField("locationX_tmp", "locationX")
//            mCoronaMapSchema?.renameField("locationY_tmp", "locationY")
        }
    },
    addFieldData { //3 add Fields
        override fun migrate(dynamicRealm: DynamicRealm) {
//            val schema: RealmSchema = dynamicRealm.schema
//            val mCoronaMapSchema: RealmObjectSchema? = schema.get("CoronaMapModel")
//            mCoronaMapSchema?.addField("synthesize", String::class.java)
//            mCoronaMapSchema?.addField("certified", Int::class.javaPrimitiveType)
//            mCoronaMapSchema?.addField("isolated", Int::class.javaPrimitiveType)
//            mCoronaMapSchema?.addField("deisolated", Int::class.javaPrimitiveType)
//            mCoronaMapSchema?.addField("dead", Int::class.javaPrimitiveType)
//            mCoronaMapSchema?.addField("percentage", Float::class.javaPrimitiveType)
        }
    },
    removeFieldData { //4 remove field data
        override fun migrate(dynamicRealm: DynamicRealm) {
//            val schema: RealmSchema = dynamicRealm.schema
//            val mCoronaMapSchema: RealmObjectSchema? = schema.get("CoronaMapModel")
//
//            mCoronaMapSchema?.removeField("synthesize")
//            mCoronaMapSchema?.removeField("certified")
//            mCoronaMapSchema?.removeField("isolated")
//            mCoronaMapSchema?.removeField("deisolated")
//            mCoronaMapSchema?.removeField("dead")
//            mCoronaMapSchema?.removeField("percentage")
        }
    },
    addCoronaLocationModel { //5 add CoronaLocationModel Table
        override fun migrate(dynamicRealm: DynamicRealm) {
//            val schema: RealmSchema = dynamicRealm.schema
//            schema.createWithPrimaryKeyField("CoronaLocationModel", "synthesize", String::class.java)
//            val mCoronaLocationModel: RealmObjectSchema? = schema.get("CoronaLocationModel")
//
//            mCoronaLocationModel?.addField("certified", Int::class.java)
//            mCoronaLocationModel?.addField("deisolated", Int::class.java)
//            mCoronaLocationModel?.addField("dead", Int::class.java)
//            mCoronaLocationModel?.addField("percentage", Float::class.java)
        }
    },
    addCoronaLocationModelForXandY { // 6 add CoronaLocationModel Field LocationX, Y
        override fun migrate(dynamicRealm: DynamicRealm) {
            val schema: RealmSchema = dynamicRealm.schema
            val mCoronaLocationModel: RealmObjectSchema? = schema.get("CoronaLocationModel")

            mCoronaLocationModel?.addField("locationX", Double::class.java)
            mCoronaLocationModel?.addField("locationY", Double::class.java)
        }
    },
    Last { //7
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