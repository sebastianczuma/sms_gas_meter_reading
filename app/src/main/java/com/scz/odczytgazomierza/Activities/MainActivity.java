package com.scz.odczytgazomierza.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.WindowManager;

import com.scz.odczytgazomierza.Database.DbHandler;
import com.scz.odczytgazomierza.Fragments.FragmentFirst;
import com.scz.odczytgazomierza.Fragments.FragmentSecond;
import com.scz.odczytgazomierza.Interfaces.PhoneNumber;
import com.scz.odczytgazomierza.R;
import com.scz.odczytgazomierza.RecyclerView.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public PendingIntent sentPendingIntent;
    public ProgressDialog progressDialog;
    public FragmentSecond fragmentSecond;
    private FragmentFirst fragmentFirst;
    private ViewPager mViewPager;
    private BroadcastReceiver checkIfSmsSentReceiver;
    private String SENT = "SMS_SENT";

    @Override
    public void onResume() {
        super.onResume();

        checkIfSmsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                progressDialog.dismiss();

                int resultCode = getResultCode();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        sendingSmsSuccess(getString(R.string.send_ok)).show();

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        String currentDateAndTime = sdf.format(new Date());
                        final DbHandler dbHandler = new DbHandler(getApplication());
                        Item item = new Item();
                        item.setDate(currentDateAndTime);
                        item.setMeterReading(fragmentFirst.meterReading.getText().toString());
                        item.setBankAccountNumber(fragmentFirst.bankAccountNumber);
                        item.setPhoneNumber(PhoneNumber.phoneNumber);
                        dbHandler.addItem(item);
                        dbHandler.close();

                        fragmentFirst.meterReading.setText("");

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        userInfo(getString(R.string.send_error_generic)).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        userInfo(getString(R.string.send_error_no_service)).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        userInfo(getString(R.string.send_error_null_pdu)).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        userInfo(getString(R.string.send_error_radio_off)).show();
                        break;
                }
            }
        };
        registerReceiver(checkIfSmsSentReceiver, new IntentFilter(SENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (checkIfSmsSentReceiver != null) {
            unregisterReceiver(checkIfSmsSentReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        fragmentFirst = new FragmentFirst();
        fragmentSecond = new FragmentSecond();

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);
    }

    private Dialog userInfo(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.attention))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog sendingSmsSuccess(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.success))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                int askedAllready = preferences.getInt("askedAllready", 0);

                if (!fragmentSecond.isReminderSet && askedAllready == 0) {
                    askUserToTurnOnNotifications(getString(R.string.notification_ask)).show();
                }
            }
        });
        return builder.create();
    }

    private Dialog askUserToTurnOnNotifications(final String message) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("askedAllready", 1);
        editor.apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.notifications))
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setCurrentItem(1);
                        fragmentSecond.manageNotifications();
                    }
                });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentFirst);
        adapter.addFragment(fragmentSecond);
        mViewPager.setAdapter(adapter);
    }

    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item, true);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }
}
