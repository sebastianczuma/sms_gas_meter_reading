package com.scz.odczytgazomierza.NotificationReminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.R;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int day = preferences.getInt("dayInt", 1);

        final Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (currentDay == day || currentDay == maxDay) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String id = "my_channel_01";
                CharSequence name = "ba";
                String description = "bla";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.GREEN);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "my_channel_01");
                mBuilder.setSmallIcon(R.drawable.ic_notifications);
                mBuilder.setContentTitle("Przypomnienie o odczycie gazu.");
                mBuilder.setContentText("Dotknij, aby przejść do aplikacji.");

                Intent resultIntent = new Intent(context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);

                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);


                mNotificationManager.notify(1, mBuilder.build());
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.drawable.ic_notifications);
                mBuilder.setContentTitle("Notification Alert, Click Me!");
                mBuilder.setContentText("Hi, This is Android Notification Detail!");
            }
        }
    }
}