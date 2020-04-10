package com.ola.travel.camera.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

/**
 * @author zhangzheng
 * @Date 2020/4/10 5:04 PM
 * @ClassName RotateLayoutUtils
 * <p>
 * Desc :
 */
public class BottomStatusUtils {
    private static String mDeviceInfo;

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context) {
        initDeviceInfo();
        if (checkNavigation(context)) {
            int totalHeight = getDpi(context);
            int contentHeight = getScreenHeight(context);
            return totalHeight - contentHeight;
        } else {
            return 0;
        }
    }

    private static void initDeviceInfo() {
        String brand = Build.BRAND;
        if (brand.equalsIgnoreCase("HUAWEI")) {
            mDeviceInfo = "navigationbar_is_min";
        } else if (brand.equalsIgnoreCase("XIAOMI")) {
            mDeviceInfo = "force_fsg_nav_bar";
        } else if (brand.equalsIgnoreCase("VIVO")) {
            mDeviceInfo = "navigation_gesture_on";
        } else if (brand.equalsIgnoreCase("OPPO")) {
            mDeviceInfo = "navigation_gesture_on";
        } else {
            mDeviceInfo = "navigationbar_is_min";
        }
    }

    private static boolean checkNavigation(Context context) {
        int navigationBarIsMin = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            navigationBarIsMin = Settings.System.getInt(context.getContentResolver(),
                    mDeviceInfo, 0);
        } else {
            if (Build.BRAND.equalsIgnoreCase("VIVO") || Build.BRAND.equalsIgnoreCase("OPPO")) {
                navigationBarIsMin = Settings.Secure.getInt(context.getContentResolver(),
                        mDeviceInfo, 0);
            } else {
                navigationBarIsMin = Settings.Global.getInt(context.getContentResolver(),
                        mDeviceInfo, 0);
            }
        }
        return navigationBarIsMin != 1;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    //获取屏幕高度 不包含虚拟按键
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
