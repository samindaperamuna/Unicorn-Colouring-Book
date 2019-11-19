package org.fifthgen.colouringbooks.controller.main;

import org.fifthgen.colouringbooks.model.bean.LocalImageBean;

import java.util.Comparator;

/**
 * Created by GameGFX Studio on 2015/9/4.
 */
public class TimestampComparator implements Comparator<LocalImageBean> {
    @Override
    public int compare(LocalImageBean localImageBean, LocalImageBean t1) {
        long diff = t1.getLastModTimeStamp() - localImageBean.getLastModTimeStamp();
        if (diff > 0)
            return 1;
        if (diff < 0)
            return -1;
        else
            return 0;
    }
}
