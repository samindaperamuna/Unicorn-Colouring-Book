package org.fifthgen.colouringbooks.util;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by GameGFX Studio on 2015/8/12.
 */
@SuppressWarnings("unused")
public class ActivityUtil {

    public static void hideStatusBar(final AppCompatActivity context) {
        hideBar(context);
        View decorView = context.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (visibility -> {
                    // Note that system bars will only be "visible" if none of the
                    // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        // adjustments to your UI, such as showing the action bar or
                        // other navigational controls.
                        showBar(context);
                        L.e("show");
                    } else {
                        // adjustments to your UI, such as hiding the action bar or
                        // other navigational controls.
                        L.e("hide");
                        hideBar(context);
                    }
                });
    }

    private static void showBar(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT < 16) {
            context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = context.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private static void hideBar(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT < 16) {
            context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = context.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
