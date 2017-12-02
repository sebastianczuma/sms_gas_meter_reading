package com.scz.odczytgazomierza.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.scz.odczytgazomierza.Database.DbHandler2;
import com.scz.odczytgazomierza.R;
import com.scz.odczytgazomierza.RecyclerView2.Item2;
import com.scz.odczytgazomierza.RecyclerView2.RecyclerViewAdapter2;

import java.util.ArrayList;
import java.util.List;

public class ListBankAccountNumber extends AppCompatActivity {
    RecyclerViewAdapter2 rcAdapter;
    List<Item2> item2List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bank_account_number);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListBankAccountNumber.this, SetBankAccountNumber.class));
            }
        });

        GridLayoutManager lLayout = new GridLayoutManager(this, 1);
        RecyclerView rView = findViewById(R.id.recycler_view);
        rView.setNestedScrollingEnabled(false);

        // Recycle View setup
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        loadDataFromDb();

        rcAdapter = new RecyclerViewAdapter2(this, item2List);
        rView.setAdapter(rcAdapter);
        SpaceItemDecoration dividerItemDecoration = new SpaceItemDecoration(20);
        rView.addItemDecoration(dividerItemDecoration);
    }

    private List<Item2> getAllItemList() {
        List<Item2> allItems = new ArrayList<>();

        final DbHandler2 dbHandler2 = new DbHandler2(this);
        for (Item2 i : dbHandler2.returnAll2()) {
            allItems.add(new Item2(
                    i.getId(),
                    i.getBankAccountNumber(),
                    i.getName()));
            Log.e("List db2", i.getBankAccountNumber());
        }
        dbHandler2.close();
        return allItems;
    }

    public void loadDataFromDb() {
        item2List = getAllItemList();
    }

    public void dataSetChanged() {
        item2List.clear();
        item2List.addAll(getAllItemList());
        rcAdapter.notifyDataSetChanged();
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
