package com.example.li.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by li on 2018/3/27 .
 * 使用SharePreferences保存用户操作的一些数据
 */

public class CacheData {
//    保存的文件文件名
    private static final String NOTE_FILE="NOTE_FILE";
    /**
     * SharedPreferences保存数据
     * @param context 上下文
     * @param key 关键字
     * @param object 需要保存的键值
     */
    public static void put(Context context,String key,Object object){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(NOTE_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (object instanceof String){
            editor.putString(key, (String) object);
        }else if(object instanceof Boolean){
            editor.putBoolean(key, (Boolean) object);
        }else if(object instanceof Integer){
            editor.putInt(key, (Integer) object);
        }
        editor.apply();
    }

    /**
     * SharedPreferences 得到数据
     * @param context 上下文
     * @param key 关键字
     * @param object 默认键值
     * @return 实际存储的键值
     */
    public static Object get(Context context,String key,Object object){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(NOTE_FILE,Context.MODE_PRIVATE);
        if (object instanceof String){
            return sharedPreferences.getString(key, (String) object);
        }else if (object instanceof Boolean){
            return sharedPreferences.getBoolean(key, (Boolean) object);
        }else if (object instanceof Integer){
            return sharedPreferences.getInt(key, (Integer) object);
        }
        return null;
    }
}
