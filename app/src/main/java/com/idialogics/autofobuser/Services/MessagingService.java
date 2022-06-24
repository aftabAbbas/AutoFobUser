package com.idialogics.autofobuser.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.idialogics.autofobuser.MainActivity;
import com.idialogics.autofobuser.R;
import com.idialogics.autofobuser.Utils.Constants;
import com.idialogics.autofobuser.Utils.Functions;
import com.idialogics.autofobuser.Utils.NotificationHelper;
import com.idialogics.autofobuser.Utils.SharedPref;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    SharedPref sh;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sh = new SharedPref(this);
        if (sh.getBoolean(Constants.IS_LOGGED_IN)) {


            Functions.uploadToken(this, s);


        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sh = new SharedPref(this);

        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);

        if (type != null) {
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                String title = remoteMessage.getData().get(Constants.KEY_TITLE);
                String body = remoteMessage.getData().get(Constants.KEY_BODY);


                if (sh.getBoolean(Constants.CHECK_IS_NOTIFICATION_ON)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        showNotificationAPI26(title, body);

                    } else {

                        showNotification(title, body);

                    }
                }

            }
        }


    }

    private void showNotificationAPI26(String title, String body) {
        NotificationHelper helper;
        Notification.Builder builder;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.FROM_NOTIFICATION, true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        helper = new NotificationHelper(this);
        builder = helper.getNotification(title, body, soundUri, resultPendingIntent);
        helper.getManager().notify(new Random().nextInt(), builder.build());
    }

    @SuppressWarnings("deprecation")
    public void showNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.FROM_NOTIFICATION, true);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

        managerCompat.notify(new Random().nextInt(), builder.build());
    }

}
