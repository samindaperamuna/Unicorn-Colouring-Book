package org.fifthgen.colouringbooks.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fifthgen.colouringbooks.util.DensityUtil;


@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class imageButtonDefineSecondLay extends LinearLayout {

    private ImageView imageViewbutton;

    private TextView textView;

    public imageButtonDefineSecondLay(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageViewbutton = new ImageView(context, attrs);
        imageViewbutton.setPadding(DensityUtil.dip2px(context, 5), DensityUtil.dip2px(context, 3), DensityUtil.dip2px(context, 5), DensityUtil.dip2px(context, 3));
        imageViewbutton.setAdjustViewBounds(true);
        imageViewbutton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        textView = new TextView(context, attrs);
        textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(11);
        textView.setPadding(0, 0, 0, DensityUtil.dip2px(context, 3));
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ProductSansRegular.ttf"));
        setClickable(true);
        setFocusable(true);
        setOrientation(LinearLayout.VERTICAL);
        addView(imageViewbutton);
        addView(textView);

    }

    public void setImageSrc(int drawableid) {
        imageViewbutton.setImageResource(drawableid);
    }
}