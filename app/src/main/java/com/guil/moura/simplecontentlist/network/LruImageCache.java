package com.guil.moura.simplecontentlist.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class LruImageCache extends LruCache<String, Bitmap> implements ImageCache {

    public LruImageCache(int maxSize) {
        super(maxSize);
    }

    public LruImageCache(Context ctx) {
        this(getCacheSize(ctx));
    }

    public static int getCacheSize(Context ctx) {

        final DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();

        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;

        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
