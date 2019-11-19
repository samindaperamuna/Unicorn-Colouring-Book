package org.fifthgen.colouringbooks.controller.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.BaseFragment;
import org.fifthgen.colouringbooks.view.EmptyRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GameGFX Studio on 2015/8/18.
 */
public class ImageWallFragment extends BaseFragment {

    private static ImageWallFragment fragment;
    @BindView(R.id.imagewall)
    EmptyRecyclerView imagewall;
    @BindView(R.id.emptylay_developing)
    LinearLayout emptylayDeveloping;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout refreshLayout;

    public static ImageWallFragment getInstance() {
        if (fragment == null) {
            fragment = new ImageWallFragment();
        }
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_imagewall, container, false);
        ButterKnife.bind(this, rootView);
        swipeRefreshLayout = refreshLayout;
        refreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.maincolor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        imagewall.setLayoutManager(linearLayoutManager);
        //ImageWallListAdapter imageWallListAdapter = new ImageWallListAdapter(getActivity(), null);
        imagewall.setEmptyView(emptylayDeveloping);
        //imagewall.setAdapter(imageWallListAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }
}
