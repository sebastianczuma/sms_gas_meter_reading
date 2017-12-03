package com.scz.odczytgazomierza.RecyclerView;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scz.odczytgazomierza.Database.DbHandler2;
import com.scz.odczytgazomierza.R;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<Item> itemList;

    public RecyclerViewAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {

        String s = "m<sup>3</sup>\t";

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.unit.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.unit.setText(Html.fromHtml(s));
        }

        holder.date.setText(itemList.get(position).getDate());
        holder.meterReading.setText(itemList.get(position).getMeterReading());

        String bankAccountNumber = itemList.get(position).getBankAccountNumber();

        bankAccountNumber = bankAccountNumber.replace(" ", "");

        for (int i = 2; i < bankAccountNumber.length(); i = i + 5) {
            bankAccountNumber = new StringBuilder(bankAccountNumber).insert(i, " ").toString();
        }

        holder.number.setText(bankAccountNumber);

        bankAccountNumber = bankAccountNumber.replace(" ", "");


        DbHandler2 dbHandler2 = new DbHandler2(holder.number.getContext());
        String fName = dbHandler2.searchName(bankAccountNumber);
        holder.name.setText(fName);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}