package com.scz.odczytgazomierza.Animations;

import android.view.animation.TranslateAnimation;

/**
 * Created by sebastianczuma on 18.12.2016.
 */

public class Animations {

    public static TranslateAnimation animate(int fromYdelta, int toYdelta) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, fromYdelta, toYdelta);
        animation.setDuration(140);
        animation.setFillAfter(false);

        return animation;
    }
}
