package com.scz.odczytgazomierza.Activities;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.scz.odczytgazomierza.Database.DbHandler;
import com.scz.odczytgazomierza.R;
import com.scz.odczytgazomierza.RecyclerView.Item;
import com.scz.odczytgazomierza.RecyclerView.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoricalData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_data);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        GridLayoutManager lLayout = new GridLayoutManager(this, 1);
        RecyclerView rView = findViewById(R.id.recycler_view);
        rView.setNestedScrollingEnabled(false);

        // Recycle View setup
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getAllItemList());
        rView.setAdapter(rcAdapter);

        SpaceItemDecoration dividerItemDecoration = new SpaceItemDecoration(20);
        rView.addItemDecoration(dividerItemDecoration);
    }

    private List<Item> getAllItemList() {
        List<Item> allItems = new ArrayList<>();

        final DbHandler dbHandler = new DbHandler(this);
        for (Item i : dbHandler.returnAll()) {
            allItems.add(new Item(
                    i.getId(),
                    i.getDate(),
                    i.getMeterReading(),
                    i.getBankAccountNumber(),
                    i.getPhoneNumber()));
        }
        dbHandler.close();
        return allItems;
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect,
                                   View view,
                                   RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = space;
            outRect.left = space;
        }
    }
}
