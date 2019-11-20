package org.fifthgen.colouringbooks.controller.main;

import android.annotation.SuppressLint;
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

    @SuppressLint("StaticFieldLeak")
    private static ImageWallFragment fragment;

    @BindView(R.id.imagewall)
    EmptyRecyclerView imageWall;

    @BindView(R.id.emptylay_developing)
    LinearLayout emptyLayDeveloping;

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

        imageWall.setLayoutManager(linearLayoutManager);
        imageWall.setEmptyView(emptyLayDeveloping);

        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));
        return rootView;
    }

}
