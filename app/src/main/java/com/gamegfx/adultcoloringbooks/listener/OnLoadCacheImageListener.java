package com.gamegfx.adultcoloringbooks.listener;

import com.gamegfx.adultcoloringbooks.model.bean.CacheImageBean;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/9/9.
 */
public interface OnLoadCacheImageListener {
    void loadCacheImageSuccess(List<CacheImageBean> cacheImageBeans);
}
