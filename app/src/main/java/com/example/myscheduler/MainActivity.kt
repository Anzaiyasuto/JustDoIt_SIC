package com.example.myscheduler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()
        list.layoutManager = LinearLayoutManager(this)


        val query = realm.where<Schedule>()
        query.equalTo("completeFlag", 0.toInt())
        val schedules = query.findAll()

        val adapter = ScheduleAdapter(schedules)
        list.adapter = adapter

        fab.setOnClickListener {
            val intent = Intent(this, ScheduleEditActivity::class.java)
            startActivity(intent)
        }
        adapter.setOnItemClickListener { id ->
            val intent = Intent(this, ScheduleEditActivity::class.java)
                .putExtra("schedule_id", id)
            startActivity(intent)
        }
/*
        adapter.setOnItemLongClickListener{ id ->
            val schedule = realm.where<Schedule>()
                .equalTo("id", id).findFirst()
            if (schedule != null) {
                schedule.completeFlag = 1
            }
        }
  */
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
