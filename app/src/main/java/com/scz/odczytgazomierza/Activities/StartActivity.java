package com.scz.odczytgazomierza.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.scz.odczytgazomierza.R;

public class StartActivity extends AppCompatActivity {
    final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        Button grantPermissions = findViewById(R.id.grant_permission);
        TextView text = findViewById(R.id.text);

        String permissionInfo1 = getString(R.string.permission_info_1);
        String permissionInfo2 = getString(R.string.permission_info_2);
        String phoneNumber =
                "<font color = \"#f58400\">" + getString(R.string.phone_number) + "</font>";

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            text.setText(Html.fromHtml(permissionInfo1 + " " + phoneNumber + " " + permissionInfo2,
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            text.setText(Html.fromHtml(permissionInfo1 + " " + phoneNumber + " " + permissionInfo2));
        }

        grantPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });

        if (doesSimExists()) {
            checkPermissionsAtStart();
        }
    }

    private void checkPermissionsAtStart() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                checkAccountNumber();
            }
        } else {
            checkAccountNumber();
        }
    }

    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                // Permission  denied
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            } else {
                // Permisson already granted
                checkAccountNumber();
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
                    checkAccountNumber();
                }
                break;
            }
        }
    }

    private void checkAccountNumber() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String number = preferences.getString("bankAccountNumber", "");

        if (!number.equals("") && number.length() == 26) {
            // Bank account number already set
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Bank account number not set
            startActivity(new Intent(this, SetBankAccountNumber.class));
            finish();
        }
    }

    private boolean doesSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY) {
            return true;
        } else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    userInfo(getString(R.string.no_sim)).show();
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    userInfo(getString(R.string.network_locked)).show();
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    userInfo(getString(R.string.sim_locked_pin)).show();
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    userInfo(getString(R.string.sim_locked_puk)).show();
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    userInfo(getString(R.string.sim_unknown)).show();
                    break;
            }
            return false;
        }
    }

    private Dialog userInfo(String message) {
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
                finish();
            }
        });
        return builder.create();
    }
}
