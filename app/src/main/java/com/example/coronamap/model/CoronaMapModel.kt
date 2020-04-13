package com.example.coronamap.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class CoronaMapModel : RealmObject() {

    @PrimaryKey
    var id: Int = 0

    @Required
    var locationX: Double? = null
    @Required
    var locationY: Double? = null

    var keyword: String? = null
    var cityName: String? = null
}
