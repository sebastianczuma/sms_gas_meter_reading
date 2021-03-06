package com.scz.odczytgazomierza.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scz.odczytgazomierza.Activities.EditBankAccountNumber;
import com.scz.odczytgazomierza.Activities.ListBankAccountNumber;
import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.R;

public class FragmentFirst extends Fragment {
    public EditText meterReading;
    public String bankAccountNumber;
    String smsBody;

    public FragmentFirst() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_first, container, false);

        meterReading = mView.findViewById(R.id.meter_reading_input);
        TextView unit = mView.findViewById(R.id.unit);
        TextView bankAccountNumberView = mView.findViewById(R.id.bank_account_number);
        TextView name = mView.findViewById(R.id.name);
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
                startActivity(new Intent(getActivity(), ListBankAccountNumber.class));
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
        name.setText(preferences.getString("numberName", ""));

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

    private Dialog userPleaseConfirm(final String message, final String reading) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setTitle(getString(R.string.confirm))
                    .setMessage(Html.fromHtml(message + " " + reading + " " + "m<sup>3</sup>\t", Html.FROM_HTML_MODE_LEGACY))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((MainActivity) getActivity()).checkPermissionsForSmsSendOreo(smsBody);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        } else {
            builder.setTitle(getString(R.string.confirm))
                    .setMessage(Html.fromHtml(message + " " + reading + " " + "m<sup>3</sup>\t"))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((MainActivity) getActivity()).checkPermissionsForSmsSendOreo(smsBody);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }
        return builder.create();
    }

    private Dialog userInfo(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.attention))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
