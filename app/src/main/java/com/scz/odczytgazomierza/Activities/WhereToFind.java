package com.scz.odczytgazomierza.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.scz.odczytgazomierza.R;

public class WhereToFind extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_to_find);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        TextView info = findViewById(R.id.where_to_find_info);

        String infoBlackText = getString(R.string.where_to_find_black);
        String infoOrangeText =
                "<font color = \"#f58400\">" + getString(R.string.where_to_find_orange) + "</font>.";


        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            info.setText(Html.fromHtml(infoBlackText + " " + infoOrangeText,
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            info.setText(Html.fromHtml(infoBlackText + " " + infoOrangeText));
        }

        SubsamplingScaleImageView imageView = findViewById(R.id.faktura);
        imageView.setImage(ImageSource.resource(R.drawable.image_facture));
    }
}
