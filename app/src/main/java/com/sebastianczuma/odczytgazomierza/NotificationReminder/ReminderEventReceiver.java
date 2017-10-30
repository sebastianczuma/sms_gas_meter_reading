package com.sebastianczuma.odczytgazomierza.NotificationReminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

public class ReminderEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    private static int whichIntentThis;
    private int hour;
    private int minute;

    public ReminderEventReceiver() {
    }

    public ReminderEventReceiver(int hour, int minute, int whichIntent) {
        this.hour = hour;
        this.minute = minute;
        whichIntentThis = whichIntent;
    }

    public static void CancelAlarm(Context context, int which) {
        whichIntentThis = which;
        PendingIntent sender = getStartPendingIntent(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, ReminderEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(
                context,
                whichIntentThis,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, ReminderEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(
                context,
                whichIntentThis,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setupAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTriggerAt(), AlarmManager.INTERVAL_DAY,
                alarmIntent);
    }

    private long getTriggerAt() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent = null;

        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            //Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
            serviceIntent = ReminderIntentService.createIntentStartNotificationService(context);
        } else if (ACTION_DELETE_NOTIFICATION.equals(action)) {
            //Log.i(getClass().getSimpleName(), "test tego onReceive delete notification action, starting notification service to handle delete");
            serviceIntent = ReminderIntentService.createIntentDeleteNotification(context);
        }

        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }
}