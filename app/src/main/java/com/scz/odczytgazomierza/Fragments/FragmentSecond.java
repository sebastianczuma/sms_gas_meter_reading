package com.scz.odczytgazomierza.Fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.scz.odczytgazomierza.Activities.HistoricalData;
import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.Animations.Animations;
import com.scz.odczytgazomierza.NotificationReminder.ReminderEventReceiver;
import com.scz.odczytgazomierza.R;

import java.util.Calendar;

public class FragmentSecond extends Fragment {
    public boolean isReminderSet;
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
            notificationInfo.setText(getString(R.string.reminder_off));
            notificationIcon.setImageResource(R.drawable.ic_notifications);
            notificationHour.setVisibility(View.VISIBLE);

            int hourInt = preferences.getInt("hourInt", 0);
            int minuteInt = preferences.getInt("minuteInt", 0);

            notificationHour.setText(setNotificationInfo(hourInt, minuteInt));
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

    private String setNotificationInfo(int hourInt, int minuteInt) {
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
        return getString(R.string.reminder_text) + " " + hourString + ":" + minuteString;
    }

    private void hideInfo() {
        notificationHour.startAnimation(Animations.animate(0, -notificationHour.getHeight()));
        notificationHour.setVisibility(View.INVISIBLE);
    }

    private void showInfo() {
        notificationHour.startAnimation(Animations.animate(-notificationHour.getHeight(), 0));
        notificationHour.setVisibility(View.VISIBLE);
    }

    private void manageNotifications() {
        final SharedPreferences.Editor editor = preferences.edit();
        if (isReminderSet) {
            int identificationNumber = 1001;
            ReminderEventReceiver.CancelAlarm(getActivity(), identificationNumber);

            notificationInfo.setText(getString(R.string.reminder_off));
            notificationIcon.setImageResource(R.drawable.ic_notifications_off);

            editor.putInt("reminderIdentification", identificationNumber);
            editor.putBoolean("isReminderSet", false);
            editor.apply();
            isReminderSet = false;

            hideInfo();
        } else {
            Calendar mcurrentTime = Calendar.getInstance();
            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker,
                                              int selectedHour,
                                              int selectedMinute) {
                            ReminderEventReceiver reminderEventReceiver
                                    = new ReminderEventReceiver(selectedHour, selectedMinute, 1001);
                            reminderEventReceiver.setupAlarm(getActivity());

                            editor.putString("hour", Integer.toString(selectedHour));
                            editor.putString("minute", Integer.toString(selectedMinute));
                            editor.putInt("hourInt", selectedHour);
                            editor.putInt("minuteInt", selectedMinute);
                            editor.putBoolean("isReminderSet", true);
                            editor.apply();

                            isReminderSet = true;
                            notificationInfo.setText(getString(R.string.reminder_on));
                            notificationIcon.setImageResource(R.drawable.ic_notifications);

                            showInfo();
                            notificationHour.setText(setNotificationInfo
                                    (selectedHour, selectedMinute));
                        }
                    },
                    hour, minute, true);

            mTimePicker.setTitle(getString(R.string.reminder_set_time));
            mTimePicker.show();
        }
    }

    public void createNewNotification() {
        final SharedPreferences.Editor editor = preferences.edit();

        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker,
                                          int selectedHour,
                                          int selectedMinute) {
                        ReminderEventReceiver reminderEventReceiver
                                = new ReminderEventReceiver(selectedHour, selectedMinute, 1001);
                        reminderEventReceiver.setupAlarm(getActivity());

                        editor.putString("hour", Integer.toString(selectedHour));
                        editor.putString("minute", Integer.toString(selectedMinute));
                        editor.putInt("hourInt", selectedHour);
                        editor.putInt("minuteInt", selectedMinute);
                        editor.putBoolean("isReminderSet", true);
                        editor.apply();

                        isReminderSet = true;
                        notificationInfo.setText(getString(R.string.reminder_on));
                        notificationIcon.setImageResource(R.drawable.ic_notifications);

                        showInfo();
                        notificationHour.setText(setNotificationInfo(selectedHour, selectedMinute));
                        assert (getActivity()) != null;
                        ((MainActivity) getActivity()).setCurrentItem(1);
                    }
                },
                hour, minute, true);
        mTimePicker.setTitle(getString(R.string.reminder_set_time));
        mTimePicker.show();
    }
}
