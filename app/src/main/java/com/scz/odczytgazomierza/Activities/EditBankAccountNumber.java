package com.scz.odczytgazomierza.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scz.odczytgazomierza.BackgroundBlur;
import com.scz.odczytgazomierza.Database.DbHandler2;
import com.scz.odczytgazomierza.R;
import com.scz.odczytgazomierza.RecyclerView2.Item2;

public class EditBankAccountNumber extends AppCompatActivity {
    public static String isNextBlurRequested = "";
    Button setBankAccountNumber;
    Button whereToFind;
    EditText newBankAccountNumber;
    EditText newNumberName;
    SharedPreferences preferences;
    String oldBankAccountNumber;
    BackgroundBlur backgroundBlur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_account_number);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setBankAccountNumber = findViewById(R.id.save);
        whereToFind = findViewById(R.id.where_to_find);
        newBankAccountNumber = findViewById(R.id.bank_account_number_input);
        newNumberName = findViewById(R.id.bank_account_name_input);

        RelativeLayout SCREEN = findViewById(R.id.activity_set_bank_account_number_relative_layout);
        ImageView SCREEN_OVERLAYING_IMAGE = findViewById(R.id.image_for_blur);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        setBankAccountNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBankAccountNumber();
            }
        });

        whereToFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), WhereToFind.class));
            }
        });

        newBankAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    checkAndModifyUserInput();
                }
                if (count > 1) {
                    newBankAccountNumber.removeTextChangedListener(this);
                    checkAndModifyUserPaste();
                    newBankAccountNumber.addTextChangedListener(this);
                }
                if (count == 0) {
                    checkAndModifyUserDelete();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        prepareBankAccountNumber();
    }

    private void prepareBankAccountNumber() {
        String bankAccountNumber = preferences.getString("bankAccountNumber", "");
        String numberName = preferences.getString("numberName", "");
        oldBankAccountNumber = preferences.getString("bankAccountNumber", "");
        newBankAccountNumber.setText(bankAccountNumber);
        newNumberName.setText(numberName);
    }

    private void checkAndModifyUserInput() {
        String bankAccountNumber = newBankAccountNumber.getText().toString();

        int currentPlace = newBankAccountNumber.getSelectionStart();
        for (int i = 2; i < currentPlace; i = i + 5) {
            if (currentPlace - 1 == i) {
                if (bankAccountNumber.charAt(currentPlace - 1) != ' ') {
                    bankAccountNumber = new StringBuilder(bankAccountNumber).insert(i, " ")
                            .toString();
                    newBankAccountNumber.setText(bankAccountNumber);
                    newBankAccountNumber.setSelection(i + 2);
                }
            }
        }
    }

    private void checkAndModifyUserPaste() {
        String toCheck = newBankAccountNumber.getText().toString();
        toCheck = toCheck.replace(" ", "");
        for (int i = 2; i < toCheck.length(); i = i + 5) {
            toCheck = new StringBuilder(toCheck).insert(i, " ").toString();
        }
        newBankAccountNumber.setText(toCheck);
        newBankAccountNumber.setSelection(newBankAccountNumber.length());
    }

    private void checkAndModifyUserDelete() {
        int currentPlace = newBankAccountNumber.getSelectionStart();
        String toCheck = newBankAccountNumber.getText().toString();
        if (currentPlace > 0) {
            if (toCheck.charAt(currentPlace - 1) == ' ') {
                String checked = toCheck.substring(0, toCheck.length() - 1);
                newBankAccountNumber.setText(checked);
            }
        }
    }

    private void saveBankAccountNumber() {
        String number = newBankAccountNumber.getText().toString();
        String name = newNumberName.getText().toString();
        number = number.replace(" ", "");

        if (number.length() == 26) {
            DbHandler2 dbHandler2 = new DbHandler2(this);
            if (dbHandler2.searchIfDbContains(number) && !number.equals(oldBankAccountNumber)) {
                userInfo("Ten numer znajduje się już na liście.").show();
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("bankAccountNumber", number);
                editor.putString("numberName", name);
                editor.apply();

                Item2 item2 = new Item2();
                item2.setBankAccountNumber(number);
                if (name.length() > 0) {
                    item2.setName(name);
                }
                if (dbHandler2.searchIfDbContains(oldBankAccountNumber)) {
                    dbHandler2.updateOneItem2(oldBankAccountNumber, item2);
                } else {
                    dbHandler2.addItem2(item2);
                }
                dbHandler2.close();

                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        } else {
            userInfo(getString(R.string.set_account_number_error_1)).show();
        }
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
}
