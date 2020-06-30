/**
 *
 */
package com.example.myscheduler

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Schedule : RealmObject() {
    @PrimaryKey var id: Long = 0 //タスクID
    var date: Date = Date() //時間(yyyymmdd)
    var progress_data: Int = 0
    var title: String = ""  //タスク名
    var detail: String = "" //タスク詳細
}