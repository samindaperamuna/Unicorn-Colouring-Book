package org.fifthgen.colouringbooks.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.fifthgen.colouringbooks.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class ImageLoaderUtil {

    public static ImageLoader imageLoader = ImageLoader.getInstance();

    private ImageLoaderUtil() {
    }

    public static ImageLoader getInstance() {
        return imageLoader;
    }

    public static DisplayImageOptions DetailImageOptionsNoCache() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.blank)
                .showImageForEmptyUri(R.mipmap.blank)
                .showImageOnFail(R.mipmap.blank).cacheInMemory(false)
                .cacheOnDisk(false).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.NONE).build();
    }

    public static DisplayImageOptions getOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(getRandomColorDrawable())
                .showImageForEmptyUri(R.mipmap.loading14)
                .showImageOnFail(R.mipmap.loading14).cacheInMemory(false)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static DisplayImageOptions getNoCacheOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(getRandomColorDrawable())
                .showImageForEmptyUri(R.mipmap.loading14)
                .showImageOnFail(R.mipmap.loading14).cacheInMemory(false)
                .cacheOnDisk(false).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static DisplayImageOptions getOpenAllCacheOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(getRandomColorDrawable())
                .showImageForEmptyUri(R.mipmap.loading14)
                .showImageOnFail(R.mipmap.loading14).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static DisplayImageOptions getOptions(Drawable loadingdrawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingdrawable)
                .showImageForEmptyUri(R.mipmap.loading14)
                .showImageOnFail(R.mipmap.loading14).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static DisplayImageOptions getNoCacheOptions(Drawable loadingdrawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingdrawable)
                .showImageForEmptyUri(R.mipmap.loading14)
                .showImageOnFail(R.mipmap.loading14).cacheInMemory(false)
                .cacheOnDisk(false).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static DisplayImageOptions getOpenAllCacheOptions(Drawable loadingdrawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingdrawable)
                .showImageForEmptyUri(R.mipmap.loading14)
                .showImageOnFail(R.mipmap.loading14).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static DisplayImageOptions DetailImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.blank)
                .showImageForEmptyUri(R.mipmap.blank)
                .showImageOnFail(R.mipmap.blank).cacheInMemory(false)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.NONE).build();
    }

    private static Drawable getRandomColorDrawable() {
        Drawable drawable;
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        drawable = new ColorDrawable(color);
        return drawable;
    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (imageUri.equals(view.getTag())) {
                L.e(String.valueOf(view.getTag()));
                ((ImageView) view).setImageBitmap(loadedImage);
                return;
            }
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
