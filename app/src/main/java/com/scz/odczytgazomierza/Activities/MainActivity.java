package com.scz.odczytgazomierza.Activities;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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

public class MainActivity extends AppCompatActivity implements PhoneNumber {
    final int PERMISSION_REQUEST_CODE = 1;
    public ProgressDialog progressDialog;
    public FragmentSecond fragmentSecond;
    private String smsBody = "";
    private FragmentFirst fragmentFirst;
    private ViewPager mViewPager;
    private BroadcastReceiver checkIfSmsSentReceiver;
    private String SMS_SENT = "SMS_SENT";

    @Override
    public void onResume() {
        super.onResume();

        checkIfSmsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressDialog.dismiss();
                switch (getResultCode()) {
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
        registerReceiver(checkIfSmsSentReceiver, new IntentFilter(SMS_SENT));
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

        fragmentFirst = new FragmentFirst();
        fragmentSecond = new FragmentSecond();

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);
    }

    public void sendSMS() {
        if (doesSimExists()) {
            try {
                progressDialog = ProgressDialog.show(this, "",
                        getString(R.string.sending_in_progress), true);

                SmsManager smsMgr = SmsManager.getDefault();
                PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(SMS_SENT), 0);
                smsMgr.sendTextMessage(phoneNumber, null, smsBody,
                        sentPendingIntent, null);

            } catch (Exception e) {
                userInfo(getString(R.string.send_error_generic));
            }
        }
    }

    public void checkPermissionsForSmsSendOreo(String smsBody) {
        this.smsBody = smsBody;
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.O) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                askForPermissionOreo().show();
            }
        } else {
            sendSMS();
        }
    }

    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.O) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                // Permission  denied
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            } else {
                // Permisson already granted
                sendSMS();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permission,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    sendSMS();
                }
                break;
            }
        }
    }

    private boolean doesSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY) {
            return true;
        } else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    userInfo(getString(R.string.no_sim_ff)).show();
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    userInfo(getString(R.string.network_locked_ff)).show();
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    userInfo(getString(R.string.sim_locked_pin_ff)).show();
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    userInfo(getString(R.string.sim_locked_puk_ff)).show();
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    userInfo(getString(R.string.sim_unknown_ff)).show();
                    break;
            }
            return false;
        }
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
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("askedAllready", 1);
                    editor.apply();
                }
            }
        });
        return builder.create();
    }

    private Dialog askUserToTurnOnNotifications(final String message) {
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

    private Dialog askForPermissionOreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_info_oreo_title))
                .setMessage(getString(R.string.permission_info_oreo))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions();
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
