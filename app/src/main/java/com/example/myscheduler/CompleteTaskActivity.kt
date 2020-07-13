package com.example.myscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_complete_task.*

class CompleteTaskActivity : AppCompatActivity() {
    private lateinit var realm:Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_task)

        realm = Realm.getDefaultInstance()
        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        val schedule = realm.where<Schedule>()
            .equalTo("id", scheduleId).findFirst()

            //textView5.text = scheduleId.toString()

            button_yes.setOnClickListener {
                realm.executeTransaction { db: Realm ->
                    val compSchedule = db.where<Schedule>()
                        .equalTo("id", scheduleId).findFirst()
                    if (compSchedule != null) {
                        compSchedule.completeFlag = 1
                    }
                }
                finish()
            }
            button_no.setOnClickListener {
                finish()
            }

    }
}