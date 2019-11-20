package org.fifthgen.colouringbooks.model;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.fifthgen.colouringbooks.MyApplication;
import org.fifthgen.colouringbooks.controller.main.ThemeListFragment;
import org.fifthgen.colouringbooks.listener.OnThemeListLoadListener;
import org.fifthgen.colouringbooks.model.bean.ThemeBean;
import org.fifthgen.colouringbooks.util.L;
import org.fifthgen.colouringbooks.util.MyHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/4.
 */
@SuppressWarnings("CanBeFinal")
public class LoadListDataAsyn extends AsyncTask {

    List<ThemeBean.Theme> themeList;
    private OnThemeListLoadListener onThemeListLoadListener;
    private String PageId = "pageid";

    @Override
    protected Object doInBackground(Object[] objects) {
        Context context = null;

        try {
            int page = (int) objects[0];

            if (objects[1] != null && objects[1] instanceof Context) {
                context = (Context) objects[1];
            }

            MyHttpClient myHttpClient = new MyHttpClient();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(PageId, String.valueOf(page)));
            String ret = myHttpClient.executePostRequest(MyApplication.ThemeListUrl, params);
            Gson gson = new Gson();
            themeList = gson.fromJson(ret, ThemeBean.class).getThemes();

            // Save to database.
            if (themeList != null) {
                if (context != null) {
                    FCDBModel.getInstance().insertNewThemes(context, themeList);
                }
            } else {
                return "FAILED";
            }

            return "SUCCESS";
        } catch (Exception e) {
            L.e(e.toString());
            return "FAILED";
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (ThemeListFragment.getInstance().isAdded() && onThemeListLoadListener != null) {
            onThemeListLoadListener.onLoadFinish(themeList);
        }
    }

    public void setOnThemeListLoadListener(OnThemeListLoadListener onThemeListLoadListener) {
        this.onThemeListLoadListener = onThemeListLoadListener;
    }
}
