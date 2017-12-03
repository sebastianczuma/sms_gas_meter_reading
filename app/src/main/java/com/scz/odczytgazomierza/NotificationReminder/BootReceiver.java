package com.scz.odczytgazomierza.NotificationReminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.scz.odczytgazomierza.Activities.MainActivity;

import java.util.Calendar;

/**
 * Created by sebastianczuma on 03.12.2017.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - Intent Action: " + intent.getAction());

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            boolean isReminderSet = preferences.getBoolean("isReminderSet", false);
            if (isReminderSet) {
                int hourInt = preferences.getInt("hourInt", 0);
                int minuteInt = preferences.getInt("minuteInt", 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourInt);
                calendar.set(Calendar.MINUTE, minuteInt);

                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent1 = new Intent(context, MainActivity.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1001, intent1, 0);

                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
            }
        }
    }
}
