package com.ola.travel.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * @author zhangzheng
 * @Date 2020/4/6 12:13 PM
 * @ClassName AutoFitSurfaceView
 * <p>
 * Desc :
 */
public class AutoFitSurfaceView extends SurfaceView {
    private float aspectRatio = 0f;

    public AutoFitSurfaceView(Context context) {
        super(context);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int w, int h) {
        if (w < 0 || h < 0) {
            return;
        }
        aspectRatio = (w * 1.0f) / (h * 1.0f);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (aspectRatio == 0f) {
            setMeasuredDimension(width, height);
        } else {
            int newWidth;
            int newHeight;
            float actualRatio;
            if (width > height) {
                actualRatio = aspectRatio;
            } else {
                actualRatio = 1f / aspectRatio;
            }
            if (width < height * actualRatio) {
                newHeight = height;
                newWidth = (int) (height * actualRatio);
            } else {
                newWidth = width;
                newHeight = (int) (width / actualRatio);
            }
            setMeasuredDimension(newWidth, newHeight);
        }
    }
}
