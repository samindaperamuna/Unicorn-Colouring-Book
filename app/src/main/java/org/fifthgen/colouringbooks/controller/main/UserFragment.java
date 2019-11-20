package org.fifthgen.colouringbooks.controller.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.BaseFragment;
import org.fifthgen.colouringbooks.factory.MyDialogFactory;
import org.fifthgen.colouringbooks.listener.OnLoadCacheImageListener;
import org.fifthgen.colouringbooks.listener.OnLoadUserPaintListener;
import org.fifthgen.colouringbooks.listener.OnLoginSuccessListener;
import org.fifthgen.colouringbooks.model.UserFragmentModel;
import org.fifthgen.colouringbooks.model.bean.LocalImageBean;
import org.fifthgen.colouringbooks.model.bean.UserBean;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.ListAnimationUtil;
import org.fifthgen.colouringbooks.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GameGFX Studio on 2015/8/18.
 */
@SuppressWarnings("unused")
public class UserFragment extends BaseFragment implements OnLoginSuccessListener {

    @SuppressLint("StaticFieldLeak")
    private static UserFragment fragment;

    RecyclerView.Adapter adapter;
    List<LocalImageBean> localImageBeans;

    @BindView(R.id.userpaintlist)
    EmptyRecyclerView userpaintlist;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout refreshLayout;


    @BindView(R.id.emptylay_paintlist)
    LinearLayout emptylayPaintlist;


    @BindView(R.id.tab_imagecache)
    RadioButton tabImagecache;

    @BindView(R.id.tab_local)
    RadioButton tabLocal;

    @BindView(R.id.usertabs)
    RadioGroup usertabs;

    MyDialogFactory myDialogFactory;

    public static UserFragment getInstance() {
        if (fragment == null) {
            fragment = new UserFragment();
        }
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, rootView);
        initViews();
        addEvents();
        return rootView;
    }

    private void initViews() {
        myDialogFactory = new MyDialogFactory(getActivity());
        userpaintlist.setEmptyView(emptylayPaintlist);
        refreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.maincolor);
        loadLocalPaints();
    }

    private void showUserLoginxDialog() {
        myDialogFactory.showLoginDialog(this);
    }

    private void addEvents() {
        swipeRefreshLayout = refreshLayout;
        refreshLayout.setOnRefreshListener(() -> {
            L.e("load");
            if (usertabs.getCheckedRadioButtonId() == R.id.tab_local) {
                // change vertical recycle view to list view
                userpaintlist.setLayoutManager(new LinearLayoutManager(getActivity()));

                // clear recycle view
                userpaintlist.setAdapter(new LocalPaintAdapter(getActivity(), localImageBeans));

                OnLoadUserPaintListener onLoadUserPaintListener = list -> {
                    if (list != null) {
                        localImageBeans = list;
                        adapter = new LocalPaintAdapter(getActivity(), localImageBeans);
                        userpaintlist.setAdapter(ListAnimationUtil.addScaleAndAlphaAnim(adapter));
                    }

                    refreshLayout.setRefreshing(false);
                };

                UserFragmentModel.getInstance(getActivity()).obtainLocalPaintList(onLoadUserPaintListener);
            } else if (usertabs.getCheckedRadioButtonId() == R.id.tab_imagecache) {
                loadCacheImages();
            }
        });

        usertabs.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.tab_local) {
                loadLocalPaints();
            } else if (i == R.id.tab_imagecache) {
                loadCacheImages();
            }
        });
    }

    private void loadCacheImages() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));

        // Change vertical recycle view to grid view.
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);

        userpaintlist.setLayoutManager(layoutManager);

        // clear recycle view.
        userpaintlist.scrollToPosition(0);
        userpaintlist.setAdapter(new CacheImageAdapter(getActivity(), new ArrayList<>()));

        // Load cache paints.
        OnLoadCacheImageListener loadUserPaintListener = cacheImageBeans -> {
            refreshLayout.setRefreshing(false);
            if (cacheImageBeans != null) {
                adapter = new CacheImageAdapter(getActivity(), cacheImageBeans);
                userpaintlist.setAdapter(ListAnimationUtil.addScaleAndAlphaAnim(adapter));
            }
        };

        UserFragmentModel.getInstance(getActivity()).obtainCacheImageList(getActivity(), loadUserPaintListener);
    }

    private void loadLocalPaints() {
        // change vertical recycle view to list view
        userpaintlist.setLayoutManager(new LinearLayoutManager(getActivity()));

        // clear recycle view
        userpaintlist.setAdapter(new LocalPaintAdapter(getActivity(), localImageBeans));

        // Load local paints
        OnLoadUserPaintListener onLoadUserPaintListener = list -> {
            if (list != null) {
                adapter = new LocalPaintAdapter(getActivity(), list);
                userpaintlist.setAdapter(adapter);
            }
        };

        UserFragmentModel.getInstance(getActivity()).obtainLocalPaintList(onLoadUserPaintListener);
    }


    @Override
    public void onResume() {
        super.onResume();

        L.e("resume  " + isAdded());

        // Always refresh list when resumed.
        if (isAdded()) {
            if (tabLocal.isChecked()) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void finish() {
        L.e("Userfinish");
        fragment = null;
    }

    @Override
    public void onLoginSuccess(UserBean userBean) {
    }
}
