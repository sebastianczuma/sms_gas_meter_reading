package com.sebastianczuma.odczytgazomierza.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sebastianczuma.odczytgazomierza.R;

class RecyclerViewHolders extends RecyclerView.ViewHolder {
    TextView date;
    TextView meterReading;
    TextView unit;

    RecyclerViewHolders(View itemView) {
        super(itemView);

        date = (TextView) itemView.findViewById(R.id.date);
        meterReading = (TextView) itemView.findViewById(R.id.meter_reading);
        unit = (TextView) itemView.findViewById(R.id.unit);
    }
}