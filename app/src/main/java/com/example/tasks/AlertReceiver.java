package com.example.tasks;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    public static final String channelID = "channelID";

    @Override
    public void onReceive(Context context, Intent intent) {
        String Task = intent.getStringExtra("Task Name");
        int taskId = intent.getIntExtra("Task ID", 1);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Reminder")
                .setContentText(Task)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.color_4))
                .build();

        notificationHelper.getManager().notify(taskId, notification);
    }
}
