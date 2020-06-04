package com.blive.View.Util.FCM

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blive.BliveApplication
import com.blive.Models.FCMModel
import com.blive.R
import com.blive.View.Activity.Testactivity
import com.blive.View.Util.Session.SessionManager
import com.blive.View.Util.db.SqlDb
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.util.*

/**
 * Created by sans on 08/11/18.
 */
class FireBaseMessagingService : FirebaseMessagingService() {
    var CHANNEL_ID = "my_channel_id"
    var sessionManager: SessionManager? = null
    var sqlDb: SqlDb? = null
    var loadurl = ""
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        sqlDb = SqlDb(this)
        sessionManager = SessionManager(this)
        Log.e(
            TAG,
            "ExceptionData: " + remoteMessage.data
        )
        /*Log.e(TAG, "ExceptionData: " + remoteMessage.getNotification().getImageUrl());*/if (remoteMessage != null) {
            if (remoteMessage.notification != null) {
                Log.d(
                    TAG,
                    "onMessageReceived: " + remoteMessage.notification
                )
                if (remoteMessage.notification!!.title.equals(
                        "pkSpecialGiftSent",
                        ignoreCase = true
                    )
                ) {
                    try {
                        val intent = Intent("pkSpecialGiftSent")
                        intent.putExtra("data", remoteMessage.notification!!.body)
                        Log.d("NotifTestApp:", "sending broadcast$intent")
                        LocalBroadcastManager.getInstance(BliveApplication.getInstance())
                            .sendBroadcast(intent)
                    } catch (e: Exception) {
                        Log.d(
                            TAG,
                            "onMessageReceived: " + e.message
                        )
                    }
                } else if (remoteMessage.notification!!.body!!.contains("User Notification Of BLIVE")) {
                    val separated =
                        remoteMessage.notification!!.body!!.split(" -- ").toTypedArray()
                    loadurl = separated[1]
                }
            }
        }
        if (remoteMessage.data.size > 0) {
            try { // Check if message contains a notification payload.
                if (remoteMessage.notification != null) {
                    try {
                        val jsonData = JSONObject(remoteMessage.data.toString())
                        handleNotification(jsonData, remoteMessage.notification!!.body)
                        Log.e(TAG, "100 Message Received: " + remoteMessage.notification!!.body)
                        val data =
                            remoteMessage.data
                        val title = data["title"]
                        val message = data["body"]
                        val image = data["image"]
                        Log.d("firebasedata", title)
                        Log.d("firebasedata", message)
                        Log.d("firebasedata", image)
                        Log.e(TAG, "No Exception: " + jsonData + "  " + remoteMessage.notification!!.body)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e(
                            TAG,
                            "ExceptionOccuredJson: " + e.message
                        )
                    }
                }
                // Check if message contains a data payload.
                if (remoteMessage.data.size > 0) {
                    try {
                        val data = remoteMessage.data
                        val title = data["title"]
                        val message = data["body"]
                        var image = data["image"]
                        Log.d("c title", title)
                        Log.d("firebasedata message", message)
                        Log.d("firebasedata", image)
                        notificationDialog(title, message, image)
                        var count =
                            sessionManager!!.getSessionStringValue("notification", "notification")!!.toInt()
                        count = count + 1
                        sessionManager!!.storeSessionStringvalue(
                            "notification",
                            "notification",
                            count.toString()
                        )
                        //                        Log.d("jsonData",jsonData.toString());
//                        Log.d("fires",jsonData.getString("body"));
//                        handleNotification(jsonData, message);
                        if (image.equals("no", ignoreCase = true)) {
                            image = ""
                        }
                        val currentDateTimeString =
                            DateFormat.getDateTimeInstance().format(Date())
                        Log.i(
                            "autolog",
                            "currentDateTimeString: $currentDateTimeString"
                        )
                        val fcmModel = FCMModel()
                        fcmModel.notificationTitle = title
                        fcmModel.notificationImage = image
                        fcmModel.date = currentDateTimeString
                        fcmModel.notificationcontent = message
                        sqlDb!!.insertNotificationData(fcmModel)
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "ExceptionOccured: " + e.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "ExceptionOccuredMain: " + e.message
                )
            }
        }
        if (remoteMessage.notification != null) {
            try {
                val jsonData = JSONObject(remoteMessage.data.toString())
                handleNotification(jsonData, remoteMessage.notification!!.body)
                Log.e(
                    TAG,
                    "No Exception: " + jsonData + "  " + remoteMessage.notification!!.body
                )
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e(
                    TAG,
                    "ExceptionOccuredJson: " + e.message
                )
            }
        }
    }

    override fun onNewToken(refreshedToken: String) {
        storeRegIdInPref(refreshedToken)
        Log.d("refreshedToken", refreshedToken)
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        val registrationComplete = Intent("registrationComplete")
        registrationComplete.putExtra("token", refreshedToken)
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
    }

    private fun storeRegIdInPref(token: String) {
        val pref = applicationContext.getSharedPreferences("ah_firebase", 0)
        val editor = pref.edit()
        editor.putString("regId", token)
        editor.apply()
    }

    private fun handleNotification(json: JSONObject, message: String?) {
        Log.d("json", json.toString())
        Log.d("message", message)
    }

    fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun notificationDialog(
        title: String?,
        message: String?,
        image: String?
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = CHANNEL_ID
        val resultIntent = Intent(applicationContext, Testactivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        resultIntent.putExtra("title", title)
        resultIntent.putExtra("message", message)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resultPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationLayout =
            RemoteViews(packageName, R.layout.custom_notification_small)
        val notificationLayoutExpanded =
            RemoteViews(packageName, R.layout.custom_notification)
        notificationLayout.setTextViewText(R.id.title, title)
        notificationLayout.setImageViewBitmap(R.id.image_app, getBitmapFromURL(image))
        notificationLayout.setTextViewText(R.id.text, message)
        notificationLayoutExpanded.setTextViewText(R.id.title, title)
        notificationLayoutExpanded.setImageViewBitmap(R.id.image_pic, getBitmapFromURL(image))
        notificationLayoutExpanded.setTextViewText(R.id.text, message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val notificationChannel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "My Notifications",
                    NotificationManager.IMPORTANCE_MAX
                )
            // Configure the notification channel.
            notificationChannel.description = message
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setSmallIcon(R.mipmap.ic_blive)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setPriority(Notification.PRIORITY_MAX)
            .setContentTitle(title)
            .setContentText(message)
            .setContentInfo(image)
        notificationManager.notify(1, notificationBuilder.build())
    }

    companion object {
        private val TAG = FireBaseMessagingService::class.java.simpleName
    }
}