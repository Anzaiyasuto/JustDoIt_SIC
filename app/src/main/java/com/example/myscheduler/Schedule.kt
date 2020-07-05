/**
 *
 */
package com.example.myscheduler

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Schedule : RealmObject() {
    @PrimaryKey var id: Long = 0 //タスクID
    var day: Date = Date() //日にち(yyyymmdd)
    var time: Date = Date() //時間(HHmm)
    var progressDate: Int = 0 //進捗度
    var title: String = ""  //タスク名
    var completeFlag: Int = 0  //完了タスク＝１、未完了＝０
}