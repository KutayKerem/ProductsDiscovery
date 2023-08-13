package com.app.kutaykerem.productdiscovery.Models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class BitmapPicassoImageCorners implements Transformation {
    private final float radius;

    public BitmapPicassoImageCorners(float radius) {
        this.radius = radius;
    }


    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap output = Bitmap.createBitmap(source.getWidth(),source.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0,source.getWidth(),source.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        canvas.drawRoundRect(rectF,radius,radius,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,rect,rect,paint);

        source.recycle();

        return output;
    }

    @Override
    public String key() {
        return "rounded_corners_transformation_" + radius;

    }
}
