package com.ola.travel.camera;


import android.content.Context;

/**
 * @author zhangzheng
 * @Date 2020/4/2 4:43 PM
 * @ClassName CameraHelper
 * <p>
 * Desc :
 */
public class CameraHelper {

    private static volatile CameraHelper singleton = null;
    private static String IMG_LOCATION;

    private CameraHelper(Context context) {
        IMG_LOCATION=context.getFilesDir().getPath()+"pic/";
    }

    public static CameraHelper getInstance(Context context) {
        if (singleton == null) {
            synchronized (CameraHelper.class) {
                if (singleton == null) {
                    singleton = new CameraHelper(context);
                }
            }
        }
        return singleton;
    }

    public void init(String imgLocation) {
        IMG_LOCATION = imgLocation;
    }

    public String getImgLocation() {
        return IMG_LOCATION;
    }
}
