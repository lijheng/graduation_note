package com.example.li.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by li on 2018/4/17.
 * 时间管理类
 */

public class DateManagement {

    /**
     * 将时间转换为yyyy年MM月格式
     * @param date 时间
     * @return yyyy年MM月 的时间字符串
     */
    public static String getStringShortDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
        return simpleDateFormat.format(date);
    }
    public static String getStringShortDate(String str){
        return str.substring(0,8);
    }

    /**
     * 将时间转换为yyyy年MM月dd日 HH:mm格式字符串
     * @param date 时间
     * @return eg:2017年12月12日 13:20
     */
    public static String getStringLongDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return simpleDateFormat.format(date);
    }

    /**
     * @param date
     * @return MM月dd日
     */
    public static String getMonthDay(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        return simpleDateFormat.format(date);
    }

    /**
     * @param date
     * @return 上午/下午 hh:mm
     */
    public static String getHourMinute(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a hh:mm", Locale.CHINESE);
        return simpleDateFormat.format(date);
    }

    public static Date transDate(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        try {
            Date date1 = simpleDateFormat.parse(date);
            return date1;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
