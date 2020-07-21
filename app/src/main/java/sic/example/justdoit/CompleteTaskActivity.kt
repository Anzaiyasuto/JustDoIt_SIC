package sic.example.justdoit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_complete_task.*

/**
 *タスクの完了動作についての処理.
 *
 * @author 原田
 * @version 0721
 */

/**
 *
 * @author 原田
 * @version 0721
 */
class CompleteTaskActivity : AppCompatActivity() {
    private lateinit var realm:Realm

    /**
     * CompleteTaskActivityの初期化等を行うメソッドです.
     * @
     * @
     */
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