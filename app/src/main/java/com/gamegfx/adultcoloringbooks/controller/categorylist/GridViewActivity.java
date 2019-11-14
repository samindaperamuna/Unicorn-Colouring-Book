package com.gamegfx.adultcoloringbooks.controller.categorylist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gamegfx.adultcoloringbooks.MyApplication;
import com.gamegfx.adultcoloringbooks.R;
import com.gamegfx.adultcoloringbooks.controller.BaseActivity;
import com.gamegfx.adultcoloringbooks.controller.paint.PaintActivity;
import com.gamegfx.adultcoloringbooks.model.GridViewActivityModel;
import com.gamegfx.adultcoloringbooks.model.OnRecycleViewItemClickListener;
import com.gamegfx.adultcoloringbooks.model.bean.PictureBean;
import com.gamegfx.adultcoloringbooks.util.L;
import com.gamegfx.adultcoloringbooks.util.ListAnimationUtil;
import com.gamegfx.adultcoloringbooks.util.NetWorkUtil;
import com.gamegfx.adultcoloringbooks.view.EmptyRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

//import com.gamegfx.colmoana.util.UmengUtil;

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
        categoryId = getIntent().getExtras().getInt(MyApplication.THEMEID);
        folderimage = MyApplication.PATHIMG + getIntent().getExtras().getString(MyApplication.FOLDERIMG);
        initViews();
    }

    private void loadLocaldata() {
        try {
            pictureBeans = getSecretGardenBean(new ArrayList<>(Arrays.asList(getAssets().list(folderimage))));
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
     * put sercet garden uri into PictureBean
     *
     * @param secretGarden
     * @return
     */
    private List<PictureBean.Picture> getSecretGardenBean(ArrayList<String> secretGarden) {
        List<PictureBean.Picture> pictureBeans = new ArrayList<>();
        for (String s : secretGarden) {
            //Log.d("url image: ", s);
            pictureBeans.add(new PictureBean.Picture(s));
        }
        return pictureBeans;
    }

    private void loadPicsInthisTheme(int anInt) {
        swipeView.post(new Runnable() {
            @Override
            public void run() {
                swipeView.setRefreshing(true);
            }
        });
        GridViewActivityModel.getInstance().loadPictureData(this, anInt, new GridViewActivityModel.OnLoadPicFinishListener() {
            @Override
            public void LoadPicFinish(List<PictureBean.Picture> pictures) {
                swipeView.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                });
                if (pictures != null && !pictures.isEmpty()) {
                    pictureBeans = pictures;
                    showGrid(false);
                } else {
                    Toast.makeText(GridViewActivity.this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void LoadPicFailed(String error) {
                swipeView.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                });
                Toast.makeText(GridViewActivity.this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
            }
        });
//        } else {
//            showGrid(false);
//        }
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
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkUtil.isNetworkConnected(GridViewActivity.this)) {
                    GridViewActivityModel.getInstance().refreshPictureData(GridViewActivity.this, categoryId, new GridViewActivityModel.OnLoadPicFinishListener() {
                        @Override
                        public void LoadPicFinish(List<PictureBean.Picture> pictureBeans) {
                            swipeView.setRefreshing(false);
                            showGrid(false);
                        }

                        @Override
                        public void LoadPicFailed(String error) {
                            swipeView.setRefreshing(false);
                            //Toast.makeText(GridViewActivity.this, getString(R.string.loadfailed), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    swipeView.setRefreshing(false);
                    //Toast.makeText(GridViewActivity.this, getString(R.string.network_notconnet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoPaintActivity(String s) {
        //UmengUtil.analysitic(this, UmengUtil.MODELNUMBER, getIntent().getStringExtra(MyApplication.THEMENAME) + categoryId);
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
        //Log.d("url", "is local: "+ folderimage);
        gridViewAdapter = new GirdRecyclerViewAdapter(this, pictureBeans, categoryId, folderimage, isLocal);
        gridViewAdapter.setOnRecycleViewItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void recycleViewItemClickListener(View view, int i) {
                if (isLocal) {
                    gotoPaintActivity(pictureBeans.get(i).getUri());
                } else {
                    gotoPaintActivity(String.format(MyApplication.ImageLageUrl, categoryId, pictureBeans.get(i).getId()));
                }
            }
        });
        gridView.setAdapter(ListAnimationUtil.addScaleandAlphaAnim(gridViewAdapter));
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (categoryId == -1) {
            loadLocaldata();
        } else {
            //loadPicsInthisTheme(categoryId);
        }*/

        loadLocaldata();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
