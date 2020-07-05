package com.example.myscheduler

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IntDef
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


        val schedules = realm.where<Schedule>().findAll()
        val alpha : Int = 0
        val schedulesTemp = schedules.where().equalTo("completeFlag", alpha).findAll()
        val adapter = ScheduleAdapter(schedulesTemp)
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

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
