package org.fifthgen.colouringbooks.controller.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.BaseFragment;
import org.fifthgen.colouringbooks.controller.categorylist.GridViewActivity;
import org.fifthgen.colouringbooks.model.ThemeListFragmentModel;
import org.fifthgen.colouringbooks.model.bean.ThemeBean;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.ListAnimationUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

/**
 * Created by GameGFX Studio on 2015/8/14.
 */
public class ThemeListFragment extends BaseFragment {
    @SuppressLint("StaticFieldLeak")
    private static ThemeListFragment fragment;
    List<ThemeBean.Theme> themeList;
    private ThemeListAdapter adapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private String search;
    private boolean isLoading;

    RecyclerView listView;
    FloatingActionButton floatingActionButton;
    Button footer;
    private AdView mAdView;
    int id = 0;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;

    public static ThemeListFragment getInstance() {
        if (fragment == null) {
            fragment = new ThemeListFragment();
        }

        fragment.setRetainInstance(true);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_theme_list, container, false);
        initViews(rootView);

        if (themeList == null) {
            loadData(LoadModel.normal);
            L.e("normal");
        } else {
            L.e("nochange");
            loadData(LoadModel.nochange);
        }

        return rootView;
    }


    @SuppressLint("InflateParams")
    private void initViews(View rootView) {
        refreshLayout = rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout = refreshLayout;
        listView = rootView.findViewById(R.id.theme_list);
        floatingActionButton = rootView.findViewById(R.id.floating);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        refreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.maincolor);

        footer = LayoutInflater.from(getActivity()).inflate(R.layout.textview_footer, null).findViewById(R.id.footer);

        footer.setVisibility(View.GONE);
        refreshLayout.setOnRefreshListener(() -> loadData(LoadModel.refresh));
        floatingActionButton.setOnClickListener(v -> listTop());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
            p.setMargins(0, 0, 0, 0); // get rid of margins since shadow area is now the margin
            floatingActionButton.setLayoutParams(p);
        }

        mAdView = new AdView(Objects.requireNonNull(this.getContext()));
        mAdView.setAdSize(AdSize.LARGE_BANNER);
        mAdView.setAdUnitId(MyApplication.BANNER_AD);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ((LinearLayout) rootView.findViewById(R.id.adView)).addView(mAdView); // add view

    }

    private void listTop() {
        listView.smoothScrollToPosition(0);
    }


    private void loadData(LoadModel model) {
        switch (model) {
            case nochange:
                addListListener();
                break;
            case normal:
                refreshLayout.post(() -> refreshLayout.setRefreshing(true));

                ThemeListFragmentModel.getInstance().loadData(getActivity(), themes -> {
                    refreshLayout.post(() -> refreshLayout.setRefreshing(false));
                    themeList = new ArrayList<>();

                    try {
                        ArrayList<String> folderList = new ArrayList<>(Arrays.asList(
                                Objects.requireNonNull(
                                        Objects.requireNonNull(getActivity()).getAssets()
                                                .list("coloring"))));
                        for (String folder : folderList) {
                            themeList.add(new ThemeBean.Theme(-1, folder, folder, 0));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    addListListener();
                });
                break;
            case refresh:
            case loadmore:
                isLoading = false;
                refreshLayout.setRefreshing(false);

                break;
        }
    }

    private void addListListener() {
        adapter = new ThemeListAdapter(getActivity(), themeList, footer);
        adapter.setOnRecycleViewItemClickListener((view, i) -> {
            if (search == null || search.isEmpty()) {
                gotoDetailGridActivity(i);
            } else {
                int pos = getThemeIndex(search, i);
                if (pos != -1) {
                    gotoDetailGridActivity(pos);
                }
            }
        });

        alphaAdapter = ListAnimationUtil.addAlphaAnim(adapter);
        listView.setAdapter(alphaAdapter);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && themeList == adapter.getList()) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        L.e("start loadmre");
                        loadData(LoadModel.loadmore);

                    }
                }
            }
        });
    }

    private int getThemeIndex(String search, int pos) {
        int index = 0;
        for (int i = 0; i < themeList.size(); i++) {
            if (themeList.get(i).getN().contains(search)) {
                if (pos == index) {
                    return i;
                }
                index++;
            }
        }

        return -1;
    }

    private void gotoDetailGridActivity(int i) {
        Intent intent = new Intent(getActivity(), GridViewActivity.class);
        intent.putExtra(MyApplication.THEMEID, themeList.get(i).getC());
        intent.putExtra(MyApplication.THEMENAME, themeList.get(i).getN());
        intent.putExtra(MyApplication.FOLDERIMG, themeList.get(i).getFolder());
        startActivity(intent);
    }

    public void filterData(String filterStr) {
        try {
            ((View) footer.getParent()).setVisibility(View.GONE);
            search = filterStr;
            List<ThemeBean.Theme> filterDateList = new ArrayList<>();
            if (filterStr.isEmpty()) {
                refreshLayout.setEnabled(true);
                filterDateList = themeList;
            } else {
                refreshLayout.setEnabled(false);
                filterDateList.clear();
                for (ThemeBean.Theme theme : themeList) {
                    if (theme.getN().toLowerCase(Locale.getDefault()).contains(filterStr.toLowerCase(Locale.getDefault())))
                        filterDateList.add(theme);
                }
            }
            adapter.updateListView(filterDateList);
            alphaAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    public void finish() {
        L.e("Themefinish");
        fragment = null;
    }

    private enum LoadModel {
        normal,
        refresh,
        loadmore,
        nochange,
    }
}
