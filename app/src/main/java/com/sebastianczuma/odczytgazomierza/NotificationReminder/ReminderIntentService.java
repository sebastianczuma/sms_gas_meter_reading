package com.sebastianczuma.odczytgazomierza.NotificationReminder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.sebastianczuma.odczytgazomierza.Activities.MainActivity;
import com.sebastianczuma.odczytgazomierza.R;

import java.util.Calendar;

public class ReminderIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public ReminderIntentService() {
        super(ReminderIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, ReminderIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (day == 1) {

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(getString(R.string.notification_title))
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(getString(R.string.notification_text))
                    .setSmallIcon(R.drawable.ic_notifications);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    new Intent(this, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(ReminderEventReceiver.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}