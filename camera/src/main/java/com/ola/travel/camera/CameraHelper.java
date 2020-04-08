package com.ola.travel.camera;


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

    private CameraHelper() {
    }

    public static CameraHelper getInstance() {
        if (singleton == null) {
            synchronized (CameraHelper.class) {
                if (singleton == null) {
                    singleton = new CameraHelper();
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
