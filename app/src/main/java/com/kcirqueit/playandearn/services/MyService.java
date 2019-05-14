package com.kcirqueit.playandearn.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kcirqueit.playandearn.LoginActivity2;
import com.kcirqueit.playandearn.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends FirebaseMessagingService {



    public MyService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        buildNotification(remoteMessage.getNotification().getBody(),
                remoteMessage.getNotification().getTitle());


    }

    public void buildNotification(String message, String title) {

        Intent intent = new Intent(this, LoginActivity2.class);
        intent.putExtra("allQuizFragment", "allQuizFragment");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel("quiz notifications", "quiz notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "quiz notification");
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setContentText(message);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

}
