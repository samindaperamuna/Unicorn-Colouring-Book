package org.fifthgen.colouringbooks.controller.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import org.fifthgen.colouringbooks.controller.AppCompatBaseAcitivity;
import org.fifthgen.colouringbooks.factory.MyDialogFactory;
import org.fifthgen.colouringbooks.factory.SharedPreferencesFactory;
import org.fifthgen.colouringbooks.listener.OnLoginSuccessListener;
import org.fifthgen.colouringbooks.model.bean.UserBean;
import org.fifthgen.colouringbooks.util.CommentUtil;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.SNSUtil;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

//import com.gamegfx.colmoana.receiver.UserLoginReceiver;

/**
 * Created by GameGFX Studio on 2018/7/31.
 */
public class MainActivity extends AppCompatBaseAcitivity {

    public static MenuItem logout;
    //UserLoginReceiver receiver;
    IntentFilter filter;
    MyDialogFactory myDialogFactory;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private long exitTime;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fonts

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/ProductSansRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        setTitle(getString(R.string.app_name));
        //UmengUtil.pushNotification(this);
        //autoLogin();
        initViews();
        showMarketCommentDialog();
        MobileAds.initialize(this, MyApplication.ADMOB_APP_ID);

        //receiver = new UserLoginReceiver();
        //filter = new IntentFilter();
        //filter.addAction("userLoginAction");
    }

    private void autoLogin() {
        MyApplication.userToken = SharedPreferencesFactory.grabString(this, SharedPreferencesFactory.USERSESSION);
        L.e(MyApplication.userToken);
        if (MyApplication.userToken != null && !MyApplication.userToken.isEmpty()) {
            Toast.makeText(this, getString(R.string.loginbg), Toast.LENGTH_SHORT).show();
            /*UmengLoginUtil.getInstance().serverBackgroundLogin(new OnLoginSuccessListener() {
                @Override
                public void onLoginSuccess(UserBean userBean) {
                    if (userBean != null && userBean.getUsers() != null)
                        LoginSuccessBroadcast.getInstance().sendBroadcast(MainActivity.this);
                }
            });*/
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
        List<String> tabs = new ArrayList<String>();
        tabs.add(getString(R.string.themelist));
        //       tabs.add(getString(R.string.imagewall));
        tabs.add(getString(R.string.userlogin));
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabs);
        //initial all fragment
        sectionsPagerAdapter.destroyAllFragment();
        viewPager.setAdapter(sectionsPagerAdapter);

        // show ad banner
        mAdView = new AdView(this);
        mAdView.setAdUnitId(MyApplication.BANNER_AD);
        mAdView.setAdSize(AdSize.BANNER);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ((LinearLayout) findViewById(R.id.adView)).addView(mAdView); // add view
        mAdView.setVisibility(View.GONE);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("position page", Integer.toString(position));
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
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_collections_white_24dp);
        //      tabLayout.getTabAt(1).setIcon(R.drawable.ic_wallpaper_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_face_white_24dp);
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mAdView.loadAd(adRequest);
    }

    private void showFirstTimeLoginDialog() {
        if (MyApplication.user == null && SharedPreferencesFactory.getBoolean(this, SharedPreferencesFactory.IsFirstTimeShowLoginDialog, true)) {
            myDialogFactory.showFirstTimeLoginDialog(new OnLoginSuccessListener() {
                @Override
                public void onLoginSuccess(UserBean userBean) {
                    //UmengLoginUtil.getInstance().loginSuccessEvent(MainActivity.this, userBean, myDialogFactory);
                }
            });
            SharedPreferencesFactory.saveBoolean(this, SharedPreferencesFactory.IsFirstTimeShowLoginDialog, false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            SNSUtil.shareApp(this);
        } else if (id == R.id.action_comment) {
            CommentUtil.commentApp(this);
        } else if (id == R.id.about) {
            myDialogFactory.showAboutDialog();
        } else if (id == R.id.action_setting) {
            myDialogFactory.showSettingDialog();
        } else if (id == R.id.action_logout) {
            //UmengLoginUtil.getInstance().logout(this);
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
        //UmengLoginUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(receiver);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
