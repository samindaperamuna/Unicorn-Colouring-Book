package org.fifthgen.colouringbooks.controller;

import android.annotation.SuppressLint;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by GameGFX Studio on 2015/9/2.
 */
@SuppressLint("Registered")
public class AppCompatBaseActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    protected AppBarLayout appBarLayout;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    public void setmSwipeRefreshLayout(SwipeRefreshLayout mSwipeRefreshLayout) {
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mSwipeRefreshLayout != null) {
            if (i == 0) {
                mSwipeRefreshLayout.setEnabled(true);
            } else {
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(this);
        }
    }

}
