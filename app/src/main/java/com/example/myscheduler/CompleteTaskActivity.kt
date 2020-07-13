package com.example.myscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where

class CompleteTaskActivity : AppCompatActivity() {
    private lateinit var realm:Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_task)

        realm = Realm.getDefaultInstance()
        val scheduleId = intent.getLongExtra("schedule_id", -1)
        val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()

    }
}