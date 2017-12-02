package com.scz.odczytgazomierza.RecyclerView2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scz.odczytgazomierza.Activities.ListBankAccountNumber;
import com.scz.odczytgazomierza.R;

import java.util.List;


public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewHolders2> {
    private List<Item2> itemList;
    private ListBankAccountNumber context;

    public RecyclerViewAdapter2(ListBankAccountNumber context, List<Item2> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_2, null);
        return new RecyclerViewHolders2(context, layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders2 holder2, int position) {
        String bankAccountNumber = itemList.get(position).getBankAccountNumber();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String checkChoosen = preferences.getString("bankAccountNumber", "");
        if (checkChoosen.equals(bankAccountNumber)) {
            holder2.check.setChecked(true);
        }

        /*for (int i = 2; i < bankAccountNumber.length(); i = i + 5) {
            bankAccountNumber = new StringBuilder(bankAccountNumber).insert(i, " ").toString();
        }*/
        holder2.name.setText(itemList.get(position).getName());
        holder2.number.setText(bankAccountNumber);


    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}