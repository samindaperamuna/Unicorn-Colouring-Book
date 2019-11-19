package org.fifthgen.colouringbooks.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.R;
import org.fifthgen.colouringbooks.factory.AnimateFactory;
import org.fifthgen.colouringbooks.model.bean.PictureBean;
import org.fifthgen.colouringbooks.model.db.FCDBHelper;
import org.fifthgen.colouringbooks.util.ImageLoaderUtil;
import org.fifthgen.colouringbooks.util.ImageSaveUtil;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.MyHttpClient;
import org.fifthgen.colouringbooks.util.NetWorkUtil;
import org.fifthgen.colouringbooks.view.MyProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/10.
 */
public class GridViewActivityModel {
    private static GridViewActivityModel gridViewActivityModel;
    private final String CATEGORY = "categoryid";
    List<PictureBean.Picture> pictureBeans;
    private String root = Environment.getExternalStorageDirectory().getPath() + "/MyFCWorks/";
    private OnLoadPicFinishListener onLoadPicFinishListener;

    private GridViewActivityModel() {
    }

    public static GridViewActivityModel getInstance() {
        if (gridViewActivityModel == null) {
            gridViewActivityModel = new GridViewActivityModel();
        }
        return gridViewActivityModel;
    }

    public void showGridLocalImageAsyn(ImageView imageView, String url) {
        if (hasSavedFile(url)) {
            AsynImageLoader.showImageAsynWithoutCache(imageView, "file:/" + root + url.hashCode() + ".png");
        } else {
            AsynImageLoader.showImageAsynWithAllCacheOpen(imageView, url);
        }
    }

    public void showGridInternetImageAsyn(ImageView imageView, final ImageView tag, String url) {
        final String bigImageUrl = url.replace("t_", "f_").replace(".jpg", ".png");
        // check have cache if have cache then add a tag
        if (DiskCacheUtils.findInCache(bigImageUrl, ImageLoader.getInstance().getDiskCache()) == null) {
            tag.setImageResource(R.drawable.ic_file_download_black_24dp);
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadPic(tag, bigImageUrl);
                }
            });
        } else {
            tag.setImageResource(R.drawable.ic_done_black_24dp);
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //do nothing just block parent listener
                }
            });
        }
        if (hasSavedFile(ImageSaveUtil.convertImageLageUrl(bigImageUrl))) {
            AsynImageLoader.showImageAsynWithoutCache(imageView, "file:/" + root + ImageSaveUtil.convertImageLageUrl(bigImageUrl).hashCode() + ".png");
        } else {
            AsynImageLoader.showImageAsynWithAllCacheOpen(imageView, url);
        }
    }

    private void downloadPic(final ImageView tag, String bigImageUrl) {
        ImageLoader.getInstance().loadImage(bigImageUrl, ImageLoaderUtil.DetailImageOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                tag.setImageResource(R.drawable.ic_autorenew_black_24dp);
                tag.startAnimation(AnimateFactory.getInstance().rotateAnimationForever(0, 360, tag.getMeasuredWidth() / 2, tag.getMeasuredHeight() / 2));
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                tag.setImageResource(R.drawable.ic_file_download_black_24dp);
                tag.clearAnimation();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                tag.setImageResource(R.drawable.ic_done_black_24dp);
                tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do nothing just block parent listener
                    }
                });
                tag.clearAnimation();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                tag.setImageResource(R.drawable.ic_file_download_black_24dp);
                tag.clearAnimation();
            }
        });
    }

    public void loadPictureData(Context context, int categoryId, OnLoadPicFinishListener onLoadPicFinishListener) {
        this.onLoadPicFinishListener = onLoadPicFinishListener;
        AsyncTask asyncTask = new LoadDetailAsyn();
        asyncTask.execute(context, categoryId, true);
    }

    public void refreshPictureData(Context context, int categoryId, OnLoadPicFinishListener onLoadPicFinishListener) {
        this.onLoadPicFinishListener = onLoadPicFinishListener;
        AsyncTask asyncTask = new LoadDetailAsyn();
        asyncTask.execute(context, categoryId, false);
    }

    private boolean hasSavedFile(String s) {
        int hashCode = s.hashCode();
        String path = root + hashCode + ".png";
        File file = new File(path);
        return file.exists();
    }

    public interface OnLoadPicFinishListener {
        void LoadPicFinish(List<PictureBean.Picture> pictureBeans);

        void LoadPicFailed(String error);
    }

    private class LoadDetailAsyn extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Context context = (Context) objects[0];
                int categoryid = (int) objects[1];
                boolean useLocalCache = (boolean) objects[2];
                //if not wifi read from db first
                if (!NetWorkUtil.isWIFI(context)) {
                    if (useLocalCache) {
                        pictureBeans = FCDBModel.getInstance().readPicList(context, categoryid);
                        if (pictureBeans != null && !pictureBeans.isEmpty()) {
                            return "SUCCESS";
                        }
                    }
                }
                MyHttpClient myHttpClient = new MyHttpClient();
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(CATEGORY, String.valueOf(categoryid)));
                String ret = myHttpClient.executePostRequest(MyApplication.ThemeDetailUrl, params);

                L.e("ret", ret);
                Gson gson = new Gson();
                pictureBeans = gson.fromJson(ret, PictureBean.class).getPictures();
                if (pictureBeans != null && !pictureBeans.isEmpty()) {
                    //delete current pictures
                    FCDBModel.getInstance().deleteThisCategoryid(context, categoryid, FCDBHelper.FCIMAGETABLE);
                    //save to db
                    FCDBModel.getInstance().insertPics(context, categoryid, pictureBeans);
                    return "SUCCESS";
                } else {
                    return "FAILED";
                }

            } catch (Exception e) {
                L.e(e.toString());
                return "FAILED";
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            MyProgressDialog.DismissDialog();

            if ("SUCCESS".equals(o)) {
                if (onLoadPicFinishListener != null) {
                    onLoadPicFinishListener.LoadPicFinish(pictureBeans);
                }
            } else {
                if (onLoadPicFinishListener != null) {
                    onLoadPicFinishListener.LoadPicFailed((String) o);
                }
            }
        }
    }
}
