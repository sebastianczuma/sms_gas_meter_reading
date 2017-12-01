package com.scz.odczytgazomierza;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;

public class BackgroundBlur {
    private static final float BITMAP_SCALE = 1.0f;
    private static final float BLUR_RADIUS = 15.0f;
    private View v;
    private ImageView iv;
    private Context context;

    public BackgroundBlur(View v, ImageView iv, Context context) {
        this.v = v;
        this.iv = iv;
        this.context = context;
    }

    public void blurBackgroundWithoutPrepare() {
        //v.setDrawingCacheEnabled(true);
        Bitmap original = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(original);
        v.draw(c);

        int width = Math.round(v.getWidth() * BITMAP_SCALE);
        int height = Math.round(v.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(original, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        iv.setImageBitmap(outputBitmap);
        v.setVisibility(View.INVISIBLE);
        iv.setVisibility(View.VISIBLE);
    }

    public void unblurBackground() {
        iv.setVisibility(View.INVISIBLE);
        v.setVisibility(View.VISIBLE);
    }
}
