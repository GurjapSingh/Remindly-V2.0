package com.blanyal.remindme;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Julian on 2/13/2018.
 ***********/

public class NotifyService extends BroadcastReceiver{

    public MainActivity main;

    @Override
    public void onReceive(Context context, Intent intent) {
        int MID = 1;
        long when = System.currentTimeMillis();

        String contact;
        contact = intent.getStringExtra("ID");
        String daily = intent.getStringExtra("DAILY");
        //Toast.makeText(context, "Alarm has been received "+intent.getStringExtra("ID"), Toast.LENGTH_LONG).show();


        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context);
        mNotifyBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("You have "+contact+" alarm(s) for today!"+daily));
        mNotifyBuilder.setSmallIcon(R.drawable.ic_alarm_on_white_24dp);
        mNotifyBuilder.setContentTitle("Remindly");
        //mNotifyBuilder.setContentText("You have "+contact+" for today!");
        mNotifyBuilder.setSound(alarmSound);
        mNotifyBuilder.setAutoCancel(true);
        mNotifyBuilder.setWhen(when);
        mNotifyBuilder.setContentIntent(pendingIntent);
        mNotifyBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(MID, mNotifyBuilder.build());
        MID++;
    }

}
