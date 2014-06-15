package com.example.SimpleDo;

/**
 * Created by James on 29/05/2014.
 */
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        //Release the lock
        wl.release();
    }

    public void setAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context){
        System.out.println("Creating alarm");
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi =  PendingIntent.getActivity(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 1), pi);
        System.out.println("Created alarm");
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
//        AlarmManager am;
//        Calendar date;
//        Intent intent = new Intent(context, NotifyService.class);
//        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
//        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
//
//        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
//        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}
