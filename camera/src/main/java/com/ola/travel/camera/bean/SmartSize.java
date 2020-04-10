package com.ola.travel.camera.bean;

import android.annotation.SuppressLint;
import android.util.Size;

/**
 * @author zhangzheng
 * @Date 2020/4/10 2:12 PM
 * @ClassName SmartSize
 * <p>
 * Desc :
 */
public class SmartSize {
    private int width;
    private int height;
    private Size size;
    private int size_long;
    private int size_short;

    @SuppressLint("NewApi")
    public SmartSize(int width, int height) {
        this.width = width;
        this.height = height;
        size = new Size(width, height);
        size_long = Math.max(size.getWidth(), size.getHeight());
        size_short = Math.min(size.getWidth(), size.getHeight());
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getSize_long() {
        return size_long;
    }

    public void setSize_long(int size_long) {
        this.size_long = size_long;
    }

    public int getSize_short() {
        return size_short;
    }

    public void setSize_short(int size_short) {
        this.size_short = size_short;
    }

    @Override
    public String toString() {
        return "SmartSize{" +
                "width=" + width +
                ", height=" + height +
                ", size=" + size +
                ", size_long=" + size_long +
                ", size_short=" + size_short +
                '}';
    }
}
