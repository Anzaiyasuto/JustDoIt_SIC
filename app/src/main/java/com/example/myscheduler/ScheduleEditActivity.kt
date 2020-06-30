package com.example.myscheduler

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.widget.SeekBar

class ScheduleEditActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        realm = Realm.getDefaultInstance()

        val scheduleId = intent?.getLongExtra("schedule_id", -1L)

        // seekbar 初期値
        progress_seekbar.progress = 0
        // seekbar 最大値
        progress_seekbar.max = 100


        if (scheduleId != -1L) {
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId).findFirst()
            dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            titleEdit.setText(schedule?.title)
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.INVISIBLE
        }

        save.setOnClickListener { view: View ->
            when (scheduleId) {
                -1L -> {
                    realm.executeTransaction { db: Realm ->
                        val maxId = db.where<Schedule>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1

                        val schedule = db.createObject<Schedule>(nextId)
                        schedule.progress_data = 0
                        val date = dateEdit.text.toString().toDate("yyyy/MM/dd")
                        if (date != null) schedule.date = date
                        schedule.title = titleEdit.text.toString()

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
                        val date = dateEdit.text.toString()
                            .toDate("yyyy/MM/dd")
                        if (date != null) schedule?.date = date
                        schedule?.title = titleEdit.text.toString()
                        progress_seekbar.progress = 0
                        if (schedule != null) {
                            progress_seekbar.progress = schedule.progress_data
                        }

                        progress_seekbar.setOnSeekBarChangeListener(
                            object : SeekBar.OnSeekBarChangeListener {
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
                .setPositiveButton("はい") { dialog, which ->
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
                .setNegativeButton("いいえ") { dialog, which ->
                    //ダイアログで「いいえ」が押された時は何もしない
                }
                .show()
        }
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
