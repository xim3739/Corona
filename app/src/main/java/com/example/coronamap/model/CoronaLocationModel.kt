package com.example.coronamap.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CoronaLocationModel : RealmObject() {

    @PrimaryKey
    var synthesize: String? = null

    var certified: Int = 0
    var deisolated: Int = 0
    var dead: Int = 0
    var percentage: Float = 0.0f

    var locationX: Double = 0.0
    var locationY: Double = 0.0
}