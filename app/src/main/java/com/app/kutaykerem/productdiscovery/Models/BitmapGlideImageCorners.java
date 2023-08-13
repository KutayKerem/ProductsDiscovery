package com.app.kutaykerem.productdiscovery.Models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class BitmapGlideImageCorners extends BitmapTransformation {

    private float radius;

    public BitmapGlideImageCorners(float radius) {
        this.radius = radius;
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool bitmapPool, @NonNull Bitmap source, int outWidth, int outHeight) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap result = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);

        return result;
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(("rounded_corners_transformation_" + radius).getBytes());
    }
}
