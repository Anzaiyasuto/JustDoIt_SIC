package sic.example.myscheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testnotificationmanagerrepeat.R
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
    TimePickerFragment.OnTimeSelectedListener {

    private lateinit var realm: Realm
    private var am: AlarmManager? = null
    private var pending: PendingIntent? = null
    private var requestCode = 1

    override fun onSelected(year: Int, month: Int, date: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, date)
        dateEdit.text = DateFormat.format("yyyy/MM/dd", c)
    }
    override fun onSelected(hourOfDay: Int, minute: Int) {
        dateEdit2.text = "%1$02d:%2$02d".format(hourOfDay, minute)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        progress_seekbar.max = 100

        //データベース用のインスタンスを用意
        //idによって新規作成か編集か使い分ける
        realm = Realm.getDefaultInstance()
        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        if (scheduleId != -1L) {
            //既存タスクの時
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId).findFirst()
            dateEdit.text = DateFormat.format("yyyy/MM/dd", schedule?.day)
            dateEdit2.text = DateFormat.format("HH:mm", schedule?.time)
            titleEdit.setText(schedule?.title)
            val c = Calendar.getInstance()
            if (schedule != null) {
                c.time = schedule.day
            }
            val timeMillis1 = c.timeInMillis
            val currentTimeMillis = System.currentTimeMillis()
            var diff = timeMillis1 - currentTimeMillis
            diff /= 1000
            diff /= 60
            diff /= 60
            diff /= 24
            if (diff > 0) {
                val str1 = "残り $diff 日"
                limit_text.text = str1
            } else {
                val str2 = "JUST DO IT !!!"
                limit_text.text = str2
                limit_text.setTextColor(Color.RED)
            }

            if (schedule != null) {
                progress_seekbar.progress = schedule.progressDate
                val str = String.format(Locale.US, "%d %%", schedule.progressDate)
                progress_text.text = str
            }
            limit_text.visibility = View.VISIBLE
            delete.visibility = View.VISIBLE
            textView3.visibility = View.VISIBLE
        } else {
            //新規タスクを見るとき
            realm.executeTransaction{ db: Realm ->
                db.where<Schedule>()
                    .equalTo("id", scheduleId).findFirst()
                dateEdit.text = DateFormat.format("yyyy/MM/dd", Date())
                dateEdit2.text = DateFormat.format("HH:mm", Date())

            }
            limit_text.visibility = View.INVISIBLE
            delete.visibility = View.INVISIBLE
            textView3.visibility = View.INVISIBLE
        }

        //日付選択
        dateEdit.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "date_dialog")
        }

        //時間選択
        dateEdit2.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }

        //保存ボタンクリック
        save.setOnClickListener {
            when (scheduleId) {
                -1L -> {
                    //データをデータベースに格納
                    realm.executeTransaction { db: Realm ->
                        val maxId = db.where<Schedule>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1

                        val newSchedule = db.createObject<Schedule>(nextId)
                        val dateDayNew = dateEdit.text.toString().toDate("yyyy/MM/dd")
                        val dateTimeNew = dateEdit2.text.toString().toDate("HH:mm")
                        if (dateDayNew != null) newSchedule.day = dateDayNew
                        if (dateTimeNew != null) newSchedule.time = dateTimeNew

                        newSchedule.title = titleEdit.text.toString()
                        newSchedule.progressDate = progress_seekbar.progress
                        if (newSchedule.progressDate == 100) {
                            newSchedule.completeFlag = 1
                            Toast.makeText(this, "COMPLETE", Toast.LENGTH_LONG).show()
                        } else {
                            newSchedule.completeFlag = 0
                        }
                        requestCode = newSchedule.id.toInt()

                        //通知処理
                        val date = "${dateEdit.text} ${dateEdit2.text}".toDate()
                        System.currentTimeMillis()

                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val intent = Intent(applicationContext, AlarmNotification::class.java)
                        intent.putExtra("RequestCode", requestCode)
                        pending = PendingIntent.getBroadcast(applicationContext, requestCode, intent, 0)
                        // アラームをセットする
                        am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (am != null) {
                            am!!.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
                            // トーストで設定されたことをを表示
                            Toast.makeText(applicationContext, "アラームをセットしました。", Toast.LENGTH_SHORT).show()
                            //Log.d("debug", "start")
                        }

                        //Toast.makeText(this, "アラームがセットできませんでした。", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    //初回の保存に関してはダイアログ表示を行わないー＞というよりも行えない（技術力不足）
                    /*
                    Snackbar.make(view, "追加しました", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.YELLOW)
                        .show()
                    */

                }
                else -> {
                    realm.executeTransaction { db: Realm ->

                        val reNewSchedule = db.where<Schedule>()
                            .equalTo("id", scheduleId).findFirst()
                        reNewSchedule?.title = titleEdit.text.toString()

                        val dateDayRenew = dateEdit.text.toString().toDate("yyyy/MM/dd")
                        val dateTimeRenew = dateEdit2.text.toString().toDate("HH:mm")
                        if (dateDayRenew != null) reNewSchedule?.day = dateDayRenew
                        if (dateTimeRenew != null) reNewSchedule?.time = dateTimeRenew

                        if (reNewSchedule != null) reNewSchedule.progressDate = progress_seekbar.progress
                        if (reNewSchedule != null) {
                            if (reNewSchedule.progressDate == 100) {
                                reNewSchedule.completeFlag = 1
                                Toast.makeText(this, "COMPLETE", Toast.LENGTH_LONG).show()
                                finish()
                                realm.close()
                            }
                        }

                        if (reNewSchedule != null) requestCode = reNewSchedule.id.toInt()

                        //通知処理
                        //val date = "${da}"

                        if (reNewSchedule != null) {
                            requestCode = reNewSchedule.id.toInt()
                        }

                        val indent = Intent(applicationContext, AlarmNotification::class.java)
                        val pending = PendingIntent.getBroadcast(
                            applicationContext, requestCode, indent, 0)

                        // アラームを解除する
                        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        am.cancel(pending)

                        val date = "${dateEdit.text} ${dateEdit2.text}".toDate()
                        when {
                            date != null -> {
                                val calendar = Calendar.getInstance()
                                calendar.time = date
                                val intent =
                                    Intent(applicationContext, AlarmNotification::class.java)
                                intent.putExtra("RequestCode", requestCode)
                                // アラームをセットする
                                am.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.timeInMillis,
                                    pending
                                )
                                // トーストで設定されたことをを表示
                                Toast.makeText(
                                    applicationContext,
                                    "alarm restart",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Log.d("debug", "start")
                            }
                            else -> Toast.makeText(this, "404 Not Found", Toast.LENGTH_LONG).show()
                        }
                        //alpha
                        /*
                        Snackbar.make(view, "修正しました", Snackbar.LENGTH_SHORT)
                            .setAction("戻る") { finish() }
                            .setActionTextColor(Color.YELLOW)
                            .show()
                    */
                        finish()
                    }

                }
            }
        }

        //消去ボタンクリック
        delete.setOnClickListener {
            //ダイアログを表示
            AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("タスクを削除しますか？")
                .setPositiveButton("はい") { _, _ ->
                    //ダイアログで「はい」が押された時はタスクを削除する
                    realm.executeTransaction { db: Realm ->
                        val deleteSchedule = db.where<Schedule>().equalTo("id", scheduleId).findFirst()
                        if (deleteSchedule != null) {
                            requestCode = deleteSchedule.id.toInt()
                        }
                        val indent = Intent(applicationContext, AlarmNotification::class.java)
                        val pending = PendingIntent.getBroadcast(
                            applicationContext, requestCode, indent, 0)

                        // アラームを解除する
                        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        am.cancel(pending)
                        Toast.makeText(applicationContext,
                            "alarm cancel", Toast.LENGTH_SHORT).show()

                        deleteSchedule?.deleteFromRealm()

                    }

                    //Snackbar.make(view, "削除しました", Snackbar.LENGTH_SHORT)
                    //    .setAction("戻る") { finish() }
                     //   .setActionTextColor(Color.YELLOW)
                       // .show()
                    finish()
                }
                .setNegativeButton("いいえ") { _, _ ->
                    //ダイアログで「いいえ」が押された時は何もしない
                }
                .show()
        }

        //シークバー操作
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

    //中断処理
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //データフォーマット
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

