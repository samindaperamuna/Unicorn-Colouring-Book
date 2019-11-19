package org.fifthgen.colouringbooks.model;

import android.content.Context;
import android.os.AsyncTask;

import org.fifthgen.colouringbooks.controller.main.UserFragment;
import org.fifthgen.colouringbooks.listener.OnLoadCacheImageListener;
import org.fifthgen.colouringbooks.listener.OnLoadUserPaintListener;
import org.fifthgen.colouringbooks.model.bean.CacheImageBean;
import org.fifthgen.colouringbooks.model.bean.LocalImageBean;
import org.fifthgen.colouringbooks.util.FileUtils;
import org.fifthgen.colouringbooks.util.L;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/9/1.
 */
public class UserFragmentModel {
    private static UserFragmentModel ourInstance;
    Context context;
    AsyncTask asyncTask;

    private UserFragmentModel(Context context) {
        this.context = context;
    }

    public static UserFragmentModel getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new UserFragmentModel(context);
        }
        return ourInstance;
    }

    public void obtainLocalPaintList(OnLoadUserPaintListener onLoadUserPaintListener) {
        asyncTask = new LoadLocalPaintsAsyn();
        asyncTask.execute(onLoadUserPaintListener);
    }

    public void obtainCacheImageList(Context context, OnLoadCacheImageListener onLoadCacheImageListener) {
        asyncTask = new LoadCacheImagesAsyn();
        asyncTask.execute(onLoadCacheImageListener, context);
    }

    private class LoadLocalPaintsAsyn extends AsyncTask {
        OnLoadUserPaintListener onLoadUserPaintListener;

        @Override
        protected Object doInBackground(Object[] objects) {
            L.e("load local data");
            onLoadUserPaintListener = (OnLoadUserPaintListener) objects[0];
            return FileUtils.obtainLocalImages();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            L.e(o.toString());
            if (UserFragment.getInstance().isAdded() && onLoadUserPaintListener != null) {
                onLoadUserPaintListener.loadUserPaintFinished((List<LocalImageBean>) o);
            }
        }
    }

    private class LoadCacheImagesAsyn extends AsyncTask {
        OnLoadCacheImageListener onLoadCacheImageListener;
        Context context;

        @Override
        protected Object doInBackground(Object[] params) {
            onLoadCacheImageListener = (OnLoadCacheImageListener) params[0];
            context = (Context) params[1];
            List<CacheImageBean> cacheImageBeans;
            cacheImageBeans = FCDBModel.getInstance().readHaveCacheImages(context);
            return cacheImageBeans;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (UserFragment.getInstance().isAdded() && onLoadCacheImageListener != null) {
                onLoadCacheImageListener.loadCacheImageSuccess((List<CacheImageBean>) o);
            }
        }
    }
}
