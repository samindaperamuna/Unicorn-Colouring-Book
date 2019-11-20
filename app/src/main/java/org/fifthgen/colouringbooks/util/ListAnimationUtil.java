package org.fifthgen.colouringbooks.util;

import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by GameGFX Studio on 2015/9/11.
 */
public class ListAnimationUtil {

    public static RecyclerView.Adapter addScaleAndAlphaAnim(RecyclerView.Adapter adapter) {
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        return new ScaleInAnimationAdapter(alphaAdapter);
    }

    public static AlphaInAnimationAdapter addAlphaAnim(RecyclerView.Adapter adapter) {
        return new AlphaInAnimationAdapter(adapter);
    }
}
