package com.gamegfx.adultcoloringbooks.listener;

import com.gamegfx.adultcoloringbooks.model.bean.ThemeBean;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/8/18.
 */
public interface OnThemeListLoadListener {
    void onLoadFinish(List<ThemeBean.Theme> names);
}
