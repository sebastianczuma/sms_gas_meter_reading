package com.sebastianczuma.odczytgazomierza.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sebastianczuma.odczytgazomierza.BackgroundBlur;
import com.sebastianczuma.odczytgazomierza.Database.DbHandler;
import com.sebastianczuma.odczytgazomierza.Fragments.FragmentFirst;
import com.sebastianczuma.odczytgazomierza.Fragments.FragmentSecond;
import com.sebastianczuma.odczytgazomierza.R;
import com.sebastianczuma.odczytgazomierza.RecyclerView.Item;
import com.sebastianczuma.odczytgazomierza.Interfaces.PhoneNumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String isNextBlurRequested = "";
    public PendingIntent sentPendingIntent;
    public BackgroundBlur backgroundBlur;
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
                if (isNextBlurRequested.equals("Wysyłanie")) {
                    backgroundBlur.unblurBackground();
                    isNextBlurRequested = "";
                }
                progressDialog.dismiss();

                int resultCode = getResultCode();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        sendingSmsSuccess(getString(R.string.send_ok)).show();

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        String currentDateandTime = sdf.format(new Date());
                        final DbHandler dbHandler = new DbHandler(getApplication());
                        Item item = new Item();
                        item.setDate(currentDateandTime);
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

        RelativeLayout SCREEN = (RelativeLayout) findViewById(R.id.activity_main_relative_layout);
        ImageView SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);

        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
    }

    private Dialog userInfo(final String message) {
        if (isNextBlurRequested.equals("")) {
            backgroundBlur.blurBackgroundWithoutPrepare();
        }

        isNextBlurRequested = message;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.attention))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isNextBlurRequested.equals(message)) {
                    backgroundBlur.unblurBackground();
                    isNextBlurRequested = "";
                }
            }
        });
        return builder.create();
    }

    private Dialog sendingSmsSuccess(final String message) {
        if (isNextBlurRequested.equals("")) {
            backgroundBlur.blurBackgroundWithoutPrepare();
        }

        isNextBlurRequested = message;

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
                if (isNextBlurRequested.equals(message)) {
                    backgroundBlur.unblurBackground();
                    isNextBlurRequested = "";

                    if (!fragmentSecond.isReminderSet) {
                        askUserToTurnOnNotifications(getString(R.string.notification_ask)).show();
                    }
                }
            }
        });
        return builder.create();
    }

    private Dialog askUserToTurnOnNotifications(final String message) {
        if (MainActivity.isNextBlurRequested.equals("")) {
            backgroundBlur.blurBackgroundWithoutPrepare();
        }

        MainActivity.isNextBlurRequested = message;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.notifications))
                .setMessage(message)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (MainActivity.isNextBlurRequested.equals(message)) {
                            backgroundBlur.unblurBackground();
                            MainActivity.isNextBlurRequested = "";
                        }
                        fragmentSecond.createNewNotification();
                    }
                });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (MainActivity.isNextBlurRequested.equals(message)) {
                    backgroundBlur.unblurBackground();
                    MainActivity.isNextBlurRequested = "";
                }
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
