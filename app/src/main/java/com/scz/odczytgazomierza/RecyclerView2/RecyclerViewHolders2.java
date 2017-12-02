package com.scz.odczytgazomierza.RecyclerView2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scz.odczytgazomierza.Activities.MainActivity;
import com.scz.odczytgazomierza.R;

class RecyclerViewHolders2 extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
    TextView name;
    TextView number;

    RecyclerViewHolders2(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        number = itemView.findViewById(R.id.number);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("bankAccountNumber", number.getText().toString());
        editor.putString("numberName", name.getText().toString());
        editor.apply();
        view.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public boolean onLongClick(final View view) {
        return false;
    }
}