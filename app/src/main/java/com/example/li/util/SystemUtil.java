package com.example.li.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by li on 2018/4/20.
 * 一些系统操作工具类
 */

public class SystemUtil {

    public static final int NEW_NOTE =1;//新建Note
    public static final int OPEN_NOTE=0;//打开note
    public static final int ITEM = 0;//    内容
    public static final int CLASS_TITLE=1;//    分类标题
    public static final int INVALID_CLICK =-1;//无效点击
    public static int dpToPx(int dpValue, Context context){
        return (int) (context.getResources().getDisplayMetrics().density*dpValue);
    }

    public static File makeDir(Context context,String fileName){
        String folderPath = context.getFilesDir().toString()+fileName;
        File folder = new File(folderPath);
        if (!folder.exists()){
            if (!folder.mkdir()){
                Toast.makeText(context, "文件创建失败", Toast.LENGTH_SHORT).show();
            }
        }
        return folder;
    }

    /**
     * 透明状态栏
     */
    public static void statusBarTransparent(Activity activity){
//        5.0
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
//                    4.4
            WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
            layoutParams.flags =
                    (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|layoutParams.flags);
        }
    }

    /**
     * 设置状态栏为亮色模式，即白底黑字
     * @param activity
     */
    public static boolean setStatusBarColor(Activity activity){
//        Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().
                    setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return true;
        }
        return false;
    }
}
