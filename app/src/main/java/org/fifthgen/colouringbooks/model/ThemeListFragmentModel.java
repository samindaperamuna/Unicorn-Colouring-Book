package org.fifthgen.colouringbooks.model;

import android.content.Context;

import org.fifthgen.colouringbooks.listener.OnThemeListLoadListener;
import org.fifthgen.colouringbooks.model.bean.ThemeBean;
import org.fifthgen.colouringbooks.model.db.FCDBHelper;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/12.
 */
@SuppressWarnings({"unused"})
public class ThemeListFragmentModel {

    private static ThemeListFragmentModel themeListFragmentModel;

    private ThemeListFragmentModel() {
    }

    public static ThemeListFragmentModel getInstance() {
        if (themeListFragmentModel == null) {
            themeListFragmentModel = new ThemeListFragmentModel();
        }
        return themeListFragmentModel;
    }

    public void loadMoreData(Context context, int page, OnThemeListLoadListener onLoadFinishListener) {
        LoadListDataAsyn loadListDataAsyn = new LoadListDataAsyn();
        loadListDataAsyn.execute(page, context);
        loadListDataAsyn.setOnThemeListLoadListener(onLoadFinishListener);
    }

    public void refreshListContent(Context context, OnThemeListLoadListener onLoadFinishListener) {
        FCDBModel.getInstance().deleteAllRows(context, FCDBHelper.FCTABLE);
        LoadListDataAsyn loadListDataAsyn = new LoadListDataAsyn();
        loadListDataAsyn.execute(0, context);
        loadListDataAsyn.setOnThemeListLoadListener(onLoadFinishListener);
    }

    public void loadData(Context context, OnThemeListLoadListener onLoadFinishListener) {
        List<ThemeBean.Theme> themeBeans = FCDBModel.getInstance().readThemeList(context);
        if (themeBeans == null) {
            LoadListDataAsyn loadListDataAsyn = new LoadListDataAsyn();
            loadListDataAsyn.execute(0, context);
            loadListDataAsyn.setOnThemeListLoadListener(onLoadFinishListener);
        } else {
            onLoadFinishListener.onLoadFinish(themeBeans);
        }
    }

}
