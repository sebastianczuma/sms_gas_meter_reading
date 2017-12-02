package com.scz.odczytgazomierza.RecyclerView2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.scz.odczytgazomierza.Activities.ListBankAccountNumber;
import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.Database.DbHandler2;
import com.scz.odczytgazomierza.R;

class RecyclerViewHolders2 extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    TextView name;
    TextView number;
    RadioButton check;
    private ListBankAccountNumber context;

    RecyclerViewHolders2(ListBankAccountNumber context, View itemView) {
        super(itemView);

        this.context = context;

        name = itemView.findViewById(R.id.name);
        number = itemView.findViewById(R.id.number);
        check = itemView.findViewById(R.id.check);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        String bankAccountNumber = number.getText().toString().replace(" ", "");

        editor.putString("bankAccountNumber", bankAccountNumber);
        editor.putString("numberName", name.getText().toString());
        editor.apply();
        view.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public boolean onLongClick(final View view) {
        String bankAccountNumber = number.getText().toString().replace(" ", "");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        String checkString = preferences.getString("bankAccountNumber", "");

        if (bankAccountNumber.equals(checkString)) {
            userInfo2(view).show();
        } else {
            userInfo(view, "Czy na pewno chcesz usunąć numer\n" + number.getText().toString() + "?").show();
        }

        return false;
    }

    private Dialog userInfo(final View view, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Uwaga")
                .setMessage(message)
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DbHandler2 dbHandler2 = new DbHandler2(view.getContext());
                        String bankAccountNumber = number.getText().toString().replace(" ", "");
                        dbHandler2.deleteOneItem2(bankAccountNumber);
                        context.dataSetChanged();
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        return builder.create();
    }

    private Dialog userInfo2(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Uwaga")
                .setMessage("Nie można usunąć aktualnie wybranego numeru.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        return builder.create();
    }
}