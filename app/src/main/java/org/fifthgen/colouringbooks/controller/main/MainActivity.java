package org.fifthgen.colouringbooks.controller.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.AppCompatBaseActivity;
import org.fifthgen.colouringbooks.factory.MyDialogFactory;
import org.fifthgen.colouringbooks.factory.SharedPreferencesFactory;
import org.fifthgen.colouringbooks.util.CommentUtil;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.SNSUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by GameGFX Studio on 2018/7/31.
 */
@SuppressWarnings({"unused", "deprecation"})
public class MainActivity extends AppCompatBaseActivity {

    public static MenuItem logout;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private long exitTime;
    private AdView mAdView;

    MyDialogFactory myDialogFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/ProductSansRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        setTitle(getString(R.string.app_name));
        initViews();
        showMarketCommentDialog();
        MobileAds.initialize(this, MyApplication.ADMOB_APP_ID);
    }

    private void autoLogin() {
        MyApplication.userToken = SharedPreferencesFactory.grabString(this, SharedPreferencesFactory.USERSESSION);
        L.e(MyApplication.userToken);

        if (MyApplication.userToken != null && !MyApplication.userToken.isEmpty()) {
            Toast.makeText(this, getString(R.string.loginbg), Toast.LENGTH_SHORT).show();
        }
    }

    private void showMarketCommentDialog() {
        if (Math.random() < 0.15 && SharedPreferencesFactory.getBoolean(this, SharedPreferencesFactory.CommentEnableKey)) {
            myDialogFactory.showCommentDialog();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_main);

        myDialogFactory = new MyDialogFactory(this);
        appBarLayout = findViewById(R.id.appBarLayout);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);

        List<String> tabs = new ArrayList<>();
        tabs.add(getString(R.string.themelist));
        tabs.add(getString(R.string.userlogin));

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabs);
        sectionsPagerAdapter.destroyAllFragment();

        viewPager.setAdapter(sectionsPagerAdapter);

        // Show Ad Banner.
        mAdView = new AdView(this);
        mAdView.setAdUnitId(MyApplication.BANNER_AD);
        mAdView.setAdSize(AdSize.BANNER);

        final AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        ((LinearLayout) findViewById(R.id.adView)).addView(mAdView);
        mAdView.setVisibility(View.GONE);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mAdView.setVisibility(View.GONE);
                } else {
                    mAdView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_collections_white_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_face_white_24dp);

        toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mAdView.loadAd(adRequest);
    }

    private void showFirstTimeLoginDialog() {
        if (MyApplication.user == null && SharedPreferencesFactory.getBoolean(this, SharedPreferencesFactory.IsFirstTimeShowLoginDialog, true)) {
            myDialogFactory.showFirstTimeLoginDialog(userBean -> {
            });

            SharedPreferencesFactory.saveBoolean(this, SharedPreferencesFactory.IsFirstTimeShowLoginDialog, false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.action_search));

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        searchView.setOnSearchClickListener(v -> viewPager.setCurrentItem(0));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ThemeListFragment.getInstance().filterData(newText);
                return true;
            }
        });

        logout = menu.findItem(R.id.action_logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            SNSUtil.shareApp(this);
        } else if (id == R.id.action_comment) {
            CommentUtil.commentApp(this);
        } else if (id == R.id.about) {
            myDialogFactory.showAboutDialog();
        } else if (id == R.id.action_setting) {
            myDialogFactory.showSettingDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.pleasepressexit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
