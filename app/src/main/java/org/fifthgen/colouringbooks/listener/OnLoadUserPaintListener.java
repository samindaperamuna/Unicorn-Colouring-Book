package org.fifthgen.colouringbooks.listener;

import org.fifthgen.colouringbooks.model.bean.LocalImageBean;

import java.util.List;

/**
 * Created by GameGFX Studio on 2015/9/1.
 */
public interface OnLoadUserPaintListener {
    void loadUserPaintFinished(List<LocalImageBean> list);
}
