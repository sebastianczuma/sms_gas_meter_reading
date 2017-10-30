package com.sebastianczuma.odczytgazomierza.NotificationReminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class ReminderServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean reminder1 = preferences.getBoolean("isReminderSet", false);

        if (reminder1) {
            int hour = preferences.getInt("hourInt", 0);
            int minute = preferences.getInt("minuteInt", 0);
            int reminder1Notification = preferences.getInt("reminderIdentification", 0);
            ReminderEventReceiver reminderEventReceiver = new ReminderEventReceiver(
                    hour,
                    minute,
                    reminder1Notification);
            reminderEventReceiver.setupAlarm(context);
        }
    }
}