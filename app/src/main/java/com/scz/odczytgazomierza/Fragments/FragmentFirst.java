package com.scz.odczytgazomierza.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scz.odczytgazomierza.Activities.BankAccountNumberList;
import com.scz.odczytgazomierza.Activities.EditBankAccountNumber;
import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.Activities.SetBankAccountNumber;
import com.scz.odczytgazomierza.Interfaces.PhoneNumber;
import com.scz.odczytgazomierza.R;

public class FragmentFirst extends Fragment implements PhoneNumber {
    public EditText meterReading;
    public String bankAccountNumber;
    private String smsBody;


    public FragmentFirst() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_first, container, false);

        meterReading = mView.findViewById(R.id.meter_reading_input);
        TextView unit = mView.findViewById(R.id.unit);
        TextView bankAccountNumberView = mView.findViewById(R.id.bank_account_number);
        Button edit = mView.findViewById(R.id.edit);
        Button send = mView.findViewById(R.id.send);
        Button choose = mView.findViewById(R.id.choose);
        ImageButton scroll = mView.findViewById(R.id.next_fragment);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditBankAccountNumber.class));
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BankAccountNumberList.class));
            }
        });

        scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setCurrentItem(1);
            }
        });

        bankAccountNumber = preferences.getString("bankAccountNumber", "");

        for (int i = 2; i < bankAccountNumber.length(); i = i + 5) {
            bankAccountNumber = new StringBuilder(bankAccountNumber).insert(i, " ").toString();
        }

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            unit.setText(Html.fromHtml("m<sup>3</sup>\t", Html.FROM_HTML_MODE_LEGACY));
        } else {
            unit.setText(Html.fromHtml("m<sup>3</sup>\t"));
        }

        bankAccountNumberView.setText(bankAccountNumber);

        return mView;
    }

    private void checkData() {
        String reading = meterReading.getText().toString();
        int readingLength = reading.length();

        if (readingLength > 0 && readingLength <= 5) {
            bankAccountNumber = bankAccountNumber.replace(" ", "");
            if (bankAccountNumber.length() == 26) {
                smsBody = bankAccountNumber + " " + reading;
                userPleaseConfirm(getString(R.string.send_confirm), reading).show();
            }
        } else {
            userInfo(getString(R.string.gas_meter_value_not_set)).show();
        }
    }

    private void sendSMS() {
        if (doesSimExists()) {
            try {
                if (MainActivity.isNextBlurRequested.equals("")) {
                    ((MainActivity) getActivity()).backgroundBlur.blurBackgroundWithoutPrepare();
                }

                MainActivity.isNextBlurRequested = getString(R.string.sending);

                ((MainActivity) getActivity()).progressDialog = ProgressDialog.show(
                        getActivity(), "", getString(R.string.sending_in_progress), true);

                SmsManager smsMgr = SmsManager.getDefault();
                smsMgr.sendTextMessage(phoneNumber, null, smsBody,
                        ((MainActivity) getActivity()).sentPendingIntent, null);

            } catch (Exception e) {
                userInfo(getString(R.string.send_error_generic));
            }
        }
    }

    public boolean doesSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager)
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
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

    private Dialog userPleaseConfirm(final String message, final String reading) {
        if (MainActivity.isNextBlurRequested.equals("")) {
            ((MainActivity) getActivity()).backgroundBlur.blurBackgroundWithoutPrepare();
        }

        MainActivity.isNextBlurRequested = message;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setTitle(getString(R.string.confirm))
                .setMessage(Html.fromHtml(message + " " + reading + " " + "m<sup>3</sup>\t", Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(getString(R.string.big_send), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendSMS();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        } else {
            builder.setTitle(getString(R.string.confirm))
                    .setMessage(Html.fromHtml(message + " " + reading + " " + "m<sup>3</sup>\t"))
                    .setPositiveButton(getString(R.string.big_send), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendSMS();
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }


        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (MainActivity.isNextBlurRequested.equals(message)) {
                    ((MainActivity) getActivity()).backgroundBlur.unblurBackground();
                    MainActivity.isNextBlurRequested = "";
                }
            }
        });
        return builder.create();
    }

    private Dialog userInfo(final String message) {
        if (MainActivity.isNextBlurRequested.equals("")) {
            ((MainActivity) getActivity()).backgroundBlur.blurBackgroundWithoutPrepare();
        }

        MainActivity.isNextBlurRequested = message;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.attention))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (MainActivity.isNextBlurRequested.equals(message)) {
                    ((MainActivity) getActivity()).backgroundBlur.unblurBackground();
                    MainActivity.isNextBlurRequested = "";
                }
            }
        });
        return builder.create();
    }
}
