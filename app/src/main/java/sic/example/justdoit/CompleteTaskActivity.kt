package sic.example.justdoit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        button_yes.setOnClickListener {
            realm.executeTransaction { db: Realm ->
                val scheduleId = intent?.getLongExtra("schedule_id", -1L)
                val compSchedule = db.where<Schedule>()
                    .equalTo("id", scheduleId).findFirst()
                if (compSchedule != null) {
                    compSchedule.completeFlag = 1
                }
            }
            realm.close()
            finish()
        }
        button_no.setOnClickListener {
            finish()
        }
    }
}