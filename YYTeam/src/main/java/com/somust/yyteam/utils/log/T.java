package com.somust.yyteam.utils.log;

/**
 * Created by 13160677911 on 2017-4-2.
 */

import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 *
 */
public class T
{

    private T()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;   //是否开启日志

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        if (isShow) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message)
    {
        if (isShow) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message)
    {
        if (isShow) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow) Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow) Toast.makeText(context, message, duration).show();
    }


    /**
     * 测试Toast（短时间）
     *
     * @param context
     * @param message
     */
    public static void testShowShort(Context context, String message)
    {
        if (isShow) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    /**
     * 测试Toast（长时间）
     *
     * @param context
     * @param message
     */
    public static void testShowLong(Context context, String message)
    {
        if (isShow) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}