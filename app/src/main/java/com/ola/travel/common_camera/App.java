package com.ola.travel.common_camera;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.ola.travel.camera.CameraHelper;


/**
 * @author zhangzheng
 * @Date 2020/4/3 12:21 PM
 * @ClassName App
 * <p>
 * Desc :
 */
public class App  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CameraHelper.getInstance().init( "/data/data/com.ola.travel.common_camera/pic/");
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
