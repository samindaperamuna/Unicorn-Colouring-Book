package org.fifthgen.colouringbooks.controller.categorylist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.controller.BaseActivity;
import org.fifthgen.colouringbooks.controller.paint.PaintActivity;
import org.fifthgen.colouringbooks.model.GridViewActivityModel;
import org.fifthgen.colouringbooks.model.bean.PictureBean;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.ListAnimationUtil;
import org.fifthgen.colouringbooks.util.NetWorkUtil;
import org.fifthgen.colouringbooks.view.EmptyRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

@SuppressWarnings("unused")
public class GridViewActivity extends BaseActivity {

    List<PictureBean.Picture> pictureBeans;
    GirdRecyclerViewAdapter gridViewAdapter;

    private int categoryId;
    private String folderimage;
    private EmptyRecyclerView gridView;
    private TextView titleView;
    private SwipeRefreshLayout swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryId = Objects.requireNonNull(getIntent().getExtras()).getInt(MyApplication.THEMEID);
        folderimage = MyApplication.PATHIMG + getIntent().getExtras().getString(MyApplication.FOLDERIMG);
        initViews();
    }

    private void loadLocalData() {
        try {
            pictureBeans = getSecretGardenBean(new ArrayList<>(Arrays.asList(Objects
                    .requireNonNull(getAssets().list(folderimage)))));

            Log.d("url size", Integer.toString(pictureBeans.size()));
            L.e(pictureBeans.size() + "");

            if (pictureBeans == null) {
                Toast.makeText(GridViewActivity.this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
            } else {
                showGrid(true);
            }
        } catch (IOException e) {
            L.e(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * put secret garden uri into PictureBean
     *
     * @param secretGarden List of URI.
     * @return List of picture bean Picture objects.
     */
    private List<PictureBean.Picture> getSecretGardenBean(ArrayList<String> secretGarden) {
        List<PictureBean.Picture> pictureBeans = new ArrayList<>();
        for (String s : secretGarden) {
            //Log.d("url image: ", s);
            pictureBeans.add(new PictureBean.Picture(s));
        }
        return pictureBeans;
    }

    private void loadPicsInThisTheme(int anInt) {
        swipeView.post(() -> swipeView.setRefreshing(true));
        GridViewActivityModel.getInstance().loadPictureData(this, anInt, new GridViewActivityModel.OnLoadPicFinishListener() {
            @Override
            public void LoadPicFinish(List<PictureBean.Picture> pictures) {
                swipeView.post(() -> swipeView.setRefreshing(false));
                if (pictures != null && !pictures.isEmpty()) {
                    pictureBeans = pictures;
                    showGrid(false);
                } else {
                    Toast.makeText(GridViewActivity.this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void LoadPicFailed(String error) {
                swipeView.post(() -> swipeView.setRefreshing(false));
                Toast.makeText(GridViewActivity.this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        setContentView(R.layout.activity_gridview);

        titleView = findViewById(R.id.toolbar_title);
        swipeView = findViewById(R.id.swiperefresh);
        gridView = findViewById(R.id.detail_gird);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        gridView.setLayoutManager(layoutManager);
        titleView.setText(getIntent().getStringExtra(MyApplication.THEMENAME));
        swipeView.setColorSchemeResources(R.color.red, R.color.orange, R.color.green, R.color.maincolor);

        swipeView.setOnRefreshListener(() -> {
            if (NetWorkUtil.isNetworkConnected(GridViewActivity.this)) {
                GridViewActivityModel.getInstance().refreshPictureData(GridViewActivity.this,
                        categoryId, new GridViewActivityModel.OnLoadPicFinishListener() {
                            @Override
                            public void LoadPicFinish(List<PictureBean.Picture> pictureBeans) {
                                swipeView.setRefreshing(false);
                                showGrid(false);
                            }

                            @Override
                            public void LoadPicFailed(String error) {
                                swipeView.setRefreshing(false);
                            }
                        });
            } else {
                swipeView.setRefreshing(false);
            }
        });
    }

    private void gotoPaintActivity(String s) {
        Intent intent = new Intent(this, PaintActivity.class);

        if (s.contains(MyApplication.MainUrl)) {
            intent.putExtra(MyApplication.BIGPIC, s);
        } else {
            Log.d("url open:", folderimage + "/" + s);
            intent.putExtra(MyApplication.BIGPIC, "assets://" + folderimage + "/" + s);
        }

        startActivity(intent);
    }


    private void showGrid(final boolean isLocal) {
        gridViewAdapter = new GirdRecyclerViewAdapter(this, pictureBeans, categoryId, folderimage, isLocal);

        gridViewAdapter.setOnRecycleViewItemClickListener((view, i) -> {
            if (isLocal) {
                gotoPaintActivity(pictureBeans.get(i).getUri());
            } else {
                gotoPaintActivity(String.format(Locale.getDefault(),
                        MyApplication.ImageLargeUrl, categoryId, pictureBeans.get(i).getId()));
            }
        });

        gridView.setAdapter(ListAnimationUtil.addScaleAndAlphaAnim(gridViewAdapter));
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadLocalData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
