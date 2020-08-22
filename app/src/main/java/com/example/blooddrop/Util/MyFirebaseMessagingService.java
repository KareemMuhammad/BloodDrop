package com.example.blooddrop.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.blooddrop.R;
import com.example.blooddrop.UI.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";
    @Override
    public void onNewToken(final String token) {
        Log.d(TAG,"token refreshed "+token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().isEmpty()){
            showNotification(remoteMessage.getNotification().getTitle()
                    , remoteMessage.getNotification().getBody());
        }else {
            showNotification(remoteMessage.getData());
        }
    }
    private void showNotification(String title, String body){
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    REMINDER_NOTIFICATION_CHANNEL_ID,
                    "Notification Blood",
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.RED);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(getApplicationContext(),R.color.black_color))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_action_location)
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }
        private void showNotification(Map<String,String> data){
        String title = data.get("title").toString();
        String body = data.get("body").toString();
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        REMINDER_NOTIFICATION_CHANNEL_ID,
                        "Notification Blood",
                        NotificationManager.IMPORTANCE_HIGH);
                mChannel.setLightColor(Color.RED);
                mChannel.enableLights(true);
                notificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),REMINDER_NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(getApplicationContext(),R.color.black_color))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_action_location)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(true);
            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }
    }
