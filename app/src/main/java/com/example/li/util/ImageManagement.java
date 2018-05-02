package com.example.li.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;

import static android.text.style.DynamicDrawableSpan.ALIGN_BOTTOM;

/**
 * Created by li on 2018/4/17.
 * 图片处理类
 */

public class ImageManagement {

    /**
     * 将字节数组转换为Bitmap
     * @param bytes 存储图片的字节数组
     * @return
     */
    public static Bitmap byteStreamToBitmap(byte []bytes){
//        调用BitmapFactory的解码方法将字节数组转换为Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bitmap;
    }

    public static ImageSpan getImageSpan(String src,Context context){
        Bitmap bitmap = BitmapFactory.decodeFile(src);
        Drawable drawable = new BitmapDrawable(bitmap);
//        获取屏幕尺寸
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
//        density = dpi/160
        float density = displayMetrics.density;
        float screenWidth = displayMetrics.widthPixels;
//        获取图片的高宽,返回值单位应是dp,px=dp*(dpi/160)
        float imgWidth = drawable.getIntrinsicWidth();
        float imgHeight = drawable.getIntrinsicHeight();
//        如果图片的宽度与屏幕宽度不一
        if (imgWidth*density!=screenWidth){
            float scale = screenWidth/imgWidth;
            imgHeight = scale*imgHeight;
            imgWidth = scale*imgWidth;
        }
        drawable.setBounds(0,0,(int)imgWidth,(int)imgHeight);
        return new ImageSpan(drawable,ALIGN_BOTTOM);
    }

    public static ImageSpan getImageSpan(int id,Context context){
        Drawable drawable = context.getDrawable(id);
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        drawable.setBounds(0,0,(int)width,(int)height);
        return new ImageSpan(drawable,ALIGN_BOTTOM);
    }

}
