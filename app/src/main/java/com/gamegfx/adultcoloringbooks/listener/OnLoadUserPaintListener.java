package com.gamegfx.adultcoloringbooks.listener;

import com.gamegfx.adultcoloringbooks.model.bean.LocalImageBean;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/9/1.
 */
public interface OnLoadUserPaintListener {
    void loadUserPaintFinished(List<LocalImageBean> list);
}
