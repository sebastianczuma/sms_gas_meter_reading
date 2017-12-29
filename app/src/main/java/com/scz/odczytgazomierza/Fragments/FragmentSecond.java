package com.scz.odczytgazomierza.Fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.scz.odczytgazomierza.Activities.HistoricalData;
import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.Animations.Animations;
import com.scz.odczytgazomierza.NotificationReminder.AlarmReceiver;
import com.scz.odczytgazomierza.R;

import java.util.Calendar;

public class FragmentSecond extends Fragment {
    public boolean isReminderSet;
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    private TextView notificationHour;
    private TextView notificationInfo;
    private ImageView notificationIcon;
    private SharedPreferences preferences;

    public FragmentSecond() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_second, container, false);

        RelativeLayout notifications = mView.findViewById(R.id.notification);
        RelativeLayout historicalData = mView.findViewById(R.id.historical_data);
        ImageButton scroll = mView.findViewById(R.id.previous_fragment);
        notificationInfo = mView.findViewById(R.id.notifications_text);
        notificationHour = mView.findViewById(R.id.reminder_text);
        notificationIcon = mView.findViewById(R.id.notifications_icon);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        isReminderSet = preferences.getBoolean("isReminderSet", false);

        if (isReminderSet) {
            notificationInfo.setText(getString(R.string.reminder_on));
            notificationIcon.setImageResource(R.drawable.ic_notifications);
            notificationHour.setVisibility(View.VISIBLE);

            int hourInt = preferences.getInt("hourInt", 0);
            int minuteInt = preferences.getInt("minuteInt", 0);
            int dayInt = preferences.getInt("dayInt", 1);

            notificationHour.setText(setNotificationInfo(dayInt, hourInt, minuteInt));
        }

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageNotifications();
            }
        });

        historicalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HistoricalData.class));
            }
        });

        scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert (getActivity()) != null;
                ((MainActivity) getActivity()).setCurrentItem(0);
            }
        });

        return mView;
    }

    private String setNotificationInfo(int dayInt, int hourInt, int minuteInt) {
        String hourString;
        String minuteString;

        if (hourInt < 10) {
            hourString = "0" + Integer.toString(hourInt);
        } else {
            hourString = Integer.toString(hourInt);
        }

        if (minuteInt < 10) {
            minuteString = "0" + Integer.toString(minuteInt);
        } else {
            minuteString = Integer.toString(minuteInt);
        }
        return "Przypomnienia będą wyświetlane " + dayInt + " dnia miesiąca o " + hourString + ":" + minuteString;
    }

    private void hideInfo() {
        notificationHour.startAnimation(Animations.animate(0, -notificationHour.getHeight()));
        notificationHour.setVisibility(View.INVISIBLE);
    }

    private void showInfo() {
        notificationHour.startAnimation(Animations.animate(-notificationHour.getHeight(), 0));
        notificationHour.setVisibility(View.VISIBLE);
    }

    public void manageNotifications() {
        alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getContext(), 1001, intent, 0);

        if (isReminderSet) {

            if (alarmMgr != null) {
                alarmMgr.cancel(alarmIntent);
            }

            notificationInfo.setText(getString(R.string.reminder_off));
            notificationIcon.setImageResource(R.drawable.ic_notifications_off);

            final SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("reminderIdentification", 1001);
            editor.putBoolean("isReminderSet", false);
            editor.apply();
            isReminderSet = false;

            hideInfo();
        } else {
            selectTime().show();
        }
    }

    public Dialog selectTime() {
        Calendar mCurrentTime = Calendar.getInstance();
        final int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectDay(selectedHour, selectedMinute).show();
                    }
                },
                hour, minute, true);
        mTimePicker.setTitle(getString(R.string.reminder_set_time));
        return mTimePicker;
    }

    public Dialog selectDay(final int selectedHour, final int selectedMinute) {
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);

        final Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        numberPicker.setValue(currentDay);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wybierz dzień miesiąca");
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final SharedPreferences.Editor editor = preferences.edit();
                int selectedDay = numberPicker.getValue();
                editor.putInt("dayInt", selectedDay);
                editor.putInt("hourInt", selectedHour);
                editor.putInt("minuteInt", selectedMinute);
                editor.putBoolean("isReminderSet", true);
                editor.apply();

                isReminderSet = true;

                setReminderAndInfo(selectedDay, selectedHour, selectedMinute);
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    void setReminderAndInfo(int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        notificationInfo.setText(getString(R.string.reminder_on));
        notificationIcon.setImageResource(R.drawable.ic_notifications);

        showInfo();
        notificationHour.setText(setNotificationInfo(day, hour, minute));
    }
}
