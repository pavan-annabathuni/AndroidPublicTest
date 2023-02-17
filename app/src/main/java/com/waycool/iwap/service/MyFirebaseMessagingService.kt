package com.waycool.iwap.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.waycool.iwap.R
import com.waycool.iwap.splash.SplashActivity
import zendesk.messaging.android.push.PushNotifications
import zendesk.messaging.android.push.PushResponsibility
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        PushNotifications.updatePushNotificationToken(s)
        token = s
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Message Data Body: ${message.data}")


        when (PushNotifications.shouldBeDisplayed(message.data)) {
            PushResponsibility.MESSAGING_SHOULD_DISPLAY -> {
                // This push belongs to Messaging and the SDK is able to display it to the end user
                PushNotifications.setNotificationSmallIconId(R.drawable.ic_outgrow_logo)
                PushNotifications.displayNotification(context = this, messageData = message.data)
            }
            PushResponsibility.MESSAGING_SHOULD_NOT_DISPLAY -> {
                // This push belongs to Messaging but it should not be displayed to the end user
            }
            PushResponsibility.NOT_FROM_MESSAGING -> {
                // This push does not belong to Messaging
                sendMyNotification(message)
            }
        }

    }

    private fun sendMyNotification(message: RemoteMessage) {

        val notificationId = NotificationID.iD
        val intent: Intent

        message.data.get("link")
        if (message.data["link"] != null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(message.data["link"])
        }
        else {
            intent = Intent(this, SplashActivity::class.java)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.identifier ="$notificationId"
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            application,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

//        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.cricket);
        val notificationBuilder = NotificationCompat.Builder(this, "outgrow")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.data["title"] ?: "")
            .setContentText(message.data["body"] ?: "")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        if (message.data["image"] != null) {
            val bitmap = getBitmapFromUrl(message.data["image"].toString())
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setLargeIcon(bitmap)
        }
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating Channel
            val notificationChannel = NotificationChannel("outgrow", "outgrow", NotificationManager.IMPORTANCE_HIGH)
            //notificationChannel.setSound(soundUri, audioAttributes);
            mNotificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)
            notificationBuilder.setChannelId("outgrow")
            //            }
        }
        mNotificationManager.notify(notificationId, notificationBuilder.build())
    }

    object NotificationID {
        private val c = AtomicInteger(0)
        val iD: Int
            get() = c.incrementAndGet()
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("awesome", "Error in getting notification image: " + e.localizedMessage)
            null
        }
    }

    companion object {
        private const val TAG = "MyFirebaseService"

        //    private Call call;
        var token: String? = null
    }

}