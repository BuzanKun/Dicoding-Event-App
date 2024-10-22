package com.dicoding.dicodingeventapp.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.dicodingeventapp.R
import com.dicoding.dicodingeventapp.data.EventRepository
import com.dicoding.dicodingeventapp.di.Injection
import com.dicoding.dicodingeventapp.ui.detail.DetailActivity

class MyWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "Buzank channel"
    }

    private val eventRepository: EventRepository = Injection.provideRepository(context)
    private var resultStatus: Result? = null

    override fun doWork(): Result {
        return getNearestEvent()
    }

    private fun getNearestEvent(): Result {
        try {
            val nearestEvent = eventRepository.getNearestEvent()
            showNotification(nearestEvent.id, nearestEvent.name, nearestEvent.beginTime)
        } catch (e: Exception) {
            resultStatus = Result.failure()
        }
        return resultStatus ?: Result.success()
    }

    private fun showNotification(id: Int?, title: String?, beginTime: String?) {
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra("id", id)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addParentStack(DetailActivity::class.java)
            addNextIntent(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    NOTIFICATION_ID,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(title)
                .setContentText("Dont Miss Out! The Event is starting soon at $beginTime")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

}