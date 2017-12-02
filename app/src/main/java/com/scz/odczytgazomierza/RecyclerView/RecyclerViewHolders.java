package com.scz.odczytgazomierza.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scz.odczytgazomierza.R;

class RecyclerViewHolders extends RecyclerView.ViewHolder {
    TextView date;
    TextView meterReading;
    TextView unit;
    TextView number;
    TextView name;

    RecyclerViewHolders(View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date);
        meterReading = itemView.findViewById(R.id.meter_reading);
        unit = itemView.findViewById(R.id.unit);
        number = itemView.findViewById(R.id.number);
        name = itemView.findViewById(R.id.name);
    }
}