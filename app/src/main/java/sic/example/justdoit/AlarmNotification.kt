package sic.example.justdoit

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Process
import android.text.format.DateFormat
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where

/**
 * アプリの通知に関しての処理.
 *
 * @author 安斎康人
 * @version 0721
 */

/**
 * @author 安斎康人
 * @version 0721
 */

class AlarmNotification : BroadcastReceiver() {
    private lateinit var realm: Realm

    // データを受信した
    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * 通知データを受信したときのメソッドです.
     *
     */
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("AlarmBroadcastReceiver", "onReceive() pid=" + Process.myPid())
        val requestCode = intent.getIntExtra("RequestCode", 0)
        val pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val channelId = "default"

        val title = context.getString(R.string.app_name)
        val currentTime = System.currentTimeMillis()


        realm = Realm.getDefaultInstance()

        val schedule = realm.where<Schedule>().equalTo("id", requestCode).findFirst()
        val showDay = DateFormat.format("MM/dd", schedule?.time)
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
            .setWhen(currentTime)
            .build()

        // 通知
        notificationManager.notify(R.string.app_name, notification)
    }
}