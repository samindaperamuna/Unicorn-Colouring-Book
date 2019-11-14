package uk.co.senab.photoview.touchevent;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.view.animation.Animation;

/**
 * Created by GameGFX Studio on 2015/9/8.
 */
public class TouchEffectFactory {
    private static TouchEffectFactory ourInstance = new TouchEffectFactory();
    TouchEffectView touchEffectView;

    private TouchEffectFactory() {
    }

    public static TouchEffectFactory getInstance() {
        return ourInstance;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showTouchEffect(final ViewGroup viewGroup, Context context, float x, float y) {
        touchEffectView = new TouchEffectView(context);
        touchEffectView.setTag("touchView");
        touchEffectView.setX(x - touchEffectView.maxSize / 2);
        touchEffectView.setY(y - touchEffectView.maxSize / 2);
        viewGroup.addView(touchEffectView);
        touchEffectView.post(new Runnable() {
            @Override
            public void run() {
                Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewGroup.removeView(viewGroup.findViewWithTag("touchView"));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                touchEffectView.showViewWithEffect(animationListener);
            }
        });

    }
}
