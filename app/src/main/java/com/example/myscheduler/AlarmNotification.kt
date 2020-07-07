package com.example.myscheduler

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Process
import android.text.format.DateFormat
import android.util.Log
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where

class AlarmNotification : BroadcastReceiver() {
    private lateinit var realm: Realm
    private val renew = 1
    // データを受信した
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("AlarmBroadcastReceiver", "onReceive() pid=" + Process.myPid())
        val requestCode = intent.getIntExtra("RequestCode", 0)
        val pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val channelId = "default"
        // app name
        val title = context.getString(R.string.app_name)
        val currentTime = System.currentTimeMillis()
        /*
        val dataFormat = SimpleDateFormat("MM/dd HH:mm", Locale.JAPAN)
        val cTime = dataFormat.format(currentTime)
*/
        // メッセージ　+ 11:22:331
        realm = Realm.getDefaultInstance()

        val schedule = realm.where<Schedule>().equalTo("id", requestCode).findFirst()
        val showDay = DateFormat.format("MM/dd", schedule?.day)
        val showTime = DateFormat.format("HH:mm", schedule?.time)

        val message = "Just Do It! : ${schedule?.title}  limit => $showDay $showTime"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Notification　Channel 設定
        val channel = NotificationChannel(
            channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = message
        channel.enableVibration(true)
        channel.canShowBadge()
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        // the channel appears on the lockscreen
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.setSound(defaultSoundUri, null)
        channel.setShowBadge(true)
        notificationManager.createNotificationChannel(channel)
        val notification = Notification.Builder(context, channelId)
            .setContentTitle(title) // android標準アイコンから
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .build()

        // 通知
        notificationManager.notify(R.string.app_name, notification)
    }
}