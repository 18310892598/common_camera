package com.ola.travel.camera.utils;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Size;
import android.view.Display;

import com.ola.travel.camera.bean.SmartSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhangzheng
 * @Date 2020/4/10 2:12 PM
 * @ClassName CameraSizes
 * <p>
 * Desc :
 */
public class CameraSizesUtils {
    /**
     * 图片和视频的标准高清尺寸
     */
    private static SmartSize SIZE_1080P = new SmartSize(1920, 1080);

    public static SmartSize getDisplaySmartSize(Display display) {
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        return new SmartSize(outPoint.x, outPoint.y);
    }

    /**
     * 根据当前屏幕的分辨率确定最合适的摄像头像素
     * 首先获取当前分辨率(最低1080)，然后遍历当前的摄像头支持像素
     *
     * @param display
     * @param localSizes
     * @return
     */
    public static Size getPreviewOutputSize(Display display, List<Camera.Size> localSizes) {
        SmartSize screenSize = getDisplaySmartSize(display);
        SmartSize maxSize;
        List<SmartSize> validSizes = new ArrayList<>();
        SmartSize equalSize;
        //判断当前的屏幕分辨率
        if (screenSize.getSize_long() >= SIZE_1080P.getSize_long() || screenSize.getSize_short() >= SIZE_1080P.getSize_short()) {
            maxSize = SIZE_1080P;
        } else {
            maxSize = screenSize;
        }
        Collections.sort(localSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                int i = o2.height - o1.height;
                if (i == 0) {
                    return o2.width - o1.width;
                }
                return i;
            }
        });
        for (Camera.Size size : localSizes) {
            equalSize = new SmartSize(size.width, size.height);
            if (equalSize.getSize_long() <= maxSize.getSize_long() && equalSize.getSize_short() <= maxSize.getSize_short()) {
                return equalSize.getSize();
            }
        }
        return maxSize.getSize();
    }
}
