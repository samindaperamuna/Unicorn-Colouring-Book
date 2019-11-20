package org.fifthgen.colouringbooks.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.fifthgen.colouringbooks.R;

import java.io.File;

/**
 * Created by GameGFX Studio on 2015/8/4.
 */
public class ShareImageUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static ShareImageUtil shareImageUtil;

    private ShareImageUtil() {
    }

    public static ShareImageUtil getInstance(Context mContext) {
        context = mContext;
        if (shareImageUtil == null) {
            shareImageUtil = new ShareImageUtil();
        }
        return shareImageUtil;
    }

    public void shareImg(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.sharemywork) + context.getString(R.string.sharecontent));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.pleaseselect)));
    }
}
