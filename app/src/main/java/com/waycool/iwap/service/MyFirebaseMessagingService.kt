package com.waycool.iwap.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.waycool.iwap.R
import com.waycool.iwap.splash.SplashActivity
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        token = s
    }

    override fun onMessageReceived(message: RemoteMessage) {
        sendMyNotification(message)
    }

    private fun sendMyNotification(message: RemoteMessage) {
        Log.d(TAG, "Message Notification Body: NULL")
        if (message.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + message.notification!!
                    .body
            )
        }
        var intent = Intent(this, SplashActivity::class.java)
        if (message.notification!!.link != null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = message.notification!!.link
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            application,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

//        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.cricket);
        val notificationBuilder = NotificationCompat.Builder(this, "outgrow")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification!!.title)
            .setContentText(message.notification!!.body)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(message.notification!!.body))
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        if (message.notification!!.imageUrl != null) {
            val bitmap = getBitmapFromUrl(message.notification!!.imageUrl.toString())
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setLargeIcon(bitmap)
        }
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            if (soundUri != null) {
            // Changing Default mode of notification

            // Creating an Audio Attribute
//                AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
//                        .build();

            // Creating Channel
            val notificationChannel =
                NotificationChannel("outgrow", "outgrow", NotificationManager.IMPORTANCE_HIGH)
            //                notificationChannel.setSound(soundUri, audioAttributes);
            mNotificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)
            notificationBuilder.setChannelId("outgrow")
            //            }
        }
        mNotificationManager.notify(NotificationID.iD, notificationBuilder.build())
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
        private const val REQUEST_CODE = 12
    }

}