package com.example.SimpleDo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by James on 30/05/2014.
 */
public class NotificationService extends Service {

    private String taskName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskName = intent.getStringExtra("TaskName");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_overflow)
                        .setContentTitle("This is a notification")
                        .setContentText("Hello World!")
                        .setAutoCancel(true);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SimpleDo.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SimpleDo.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(R.menu.action_bar, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
