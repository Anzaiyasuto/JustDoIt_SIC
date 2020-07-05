package com.example.myscheduler

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testnotificationmanagerrepeat.R
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


//TimerAlertDialog.Listenerを継承する（可能ならば）
class ScheduleEditActivity : AppCompatActivity(),
    DatePickerFragment.OnDateSelectedListener,
    TimePickerFragment.OnTimeSelectedListener{


    override fun onSelected(year: Int, month: Int, date: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, date)
        dateEdit.text = DateFormat.format("yyyy/MM/dd", c)
    }

    override fun onSelected(hourOfDay: Int, minute: Int) {
        dateEdit2.text = "%1$02d:%2$02d".format(hourOfDay, minute)
    }


    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        progress_seekbar.max = 100

        //データベース用のインスタンスを用意
        realm = Realm.getDefaultInstance()
        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        if (scheduleId != -1L) {
            //既存タスクの時
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId).findFirst()
            dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.day))
            dateEdit2.setText(DateFormat.format("HH:mm", schedule?.time))
            titleEdit.setText(schedule?.title)
            if (schedule != null) {
                progress_seekbar.progress = schedule.progress_data
                val str = String.format(Locale.US, "%d %%", schedule.progress_data)
                progress_text.text = str
            }
            delete.visibility = View.VISIBLE
        } else {
            //新規タスクを見るとき
            realm.executeTransaction{ db: Realm ->
                db.where<Schedule>()
                    .equalTo("id", scheduleId).findFirst()

            }

            delete.visibility = View.INVISIBLE
        }

        dateEdit.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "date_dialog")
        }
        dateEdit2.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }
        save.setOnClickListener { view: View ->
            when (scheduleId) {
                -1L -> {
                    realm.executeTransaction { db: Realm ->
                        val maxId = db.where<Schedule>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1

                        val schedule = db.createObject<Schedule>(nextId)
                        val dateDayNew = dateEdit.text.toString().toDate("yyyy/MM/dd")
                        val dateTimeNew = dateEdit2.text.toString().toDate("HH:mm")
                        if (dateDayNew != null) schedule.day = dateDayNew
                        if (dateTimeNew != null) schedule.time = dateTimeNew
                        schedule.title = titleEdit.text.toString()
                        schedule.progress_data = progress_seekbar.progress
                        schedule.completeFlag = 0
                    }
                    Snackbar.make(view, "追加しました", Snackbar.LENGTH_SHORT)
                        .setAction("戻る") { finish() }
                        .setActionTextColor(Color.YELLOW)
                        .show()
                }
                else -> {
                    realm.executeTransaction { db: Realm ->

                        val schedule = db.where<Schedule>()
                            .equalTo("id", scheduleId).findFirst()
                        schedule?.title = titleEdit.text.toString()

                        val dateDayRenew = dateEdit.text.toString().toDate("yyyy/MM/dd")
                        val dateTimeRenew = dateEdit2.text.toString().toDate("HH:mm")
                        if (dateDayRenew != null) schedule?.day = dateDayRenew
                        if (dateTimeRenew != null) schedule?.time = dateTimeRenew
                        if (schedule != null) schedule.progress_data = progress_seekbar.progress
                    }
                    Snackbar.make(view, "修正しました", Snackbar.LENGTH_SHORT)
                        .setAction("戻る") { finish() }
                        .setActionTextColor(Color.YELLOW)
                        .show()
                }
            }
        }
        delete.setOnClickListener {view: View ->

            //ダイアログを表示
            AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("タスクを削除しますか？")
                .setPositiveButton("はい") { _, _ ->
                    //ダイアログで「はい」が押された時はタスクを削除する
                    realm.executeTransaction { db: Realm ->
                        db.where<Schedule>().equalTo("id", scheduleId)
                            ?.findFirst()
                            ?.deleteFromRealm()
                    }
                    Snackbar.make(view, "削除しました", Snackbar.LENGTH_SHORT)
                        .setAction("戻る") { finish() }
                        .setActionTextColor(Color.YELLOW)
                        .show()
                }
                .setNegativeButton("いいえ") { _, _ ->
                    //ダイアログで「いいえ」が押された時は何もしない
                }
                .show()
        }
        progress_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                //ツマミがドラッグされると呼ばれる
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    // 68 % のようにフォーマト、
                    // この場合、Locale.USが汎用的に推奨される
                    val str = String.format(Locale.US, "%d %%", progress)
                    progress_text.text = str
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // ツマミがタッチされた時に呼ばれる
                }
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // ツマミがリリースされた時に呼ばれる
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        return try {
            SimpleDateFormat(pattern).parse(this)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: ParseException) {
            return null
        }
    }


}
