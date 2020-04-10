package com.ola.travel.camera.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ola.travel.camera.bean.OlaCameraMedia;
import com.ola.travel.camera.utils.CameraConstant;
import com.ola.travel.camera.R;
import com.ola.travel.camera.helper.CameraPresenter;
import com.ola.travel.camera.utils.BottomStatusUtils;
import com.ola.travel.camera.view.AutoFitSurfaceView;

import java.io.File;

/**
 * @author zhangzheng
 * @Date 2020/3/27 2:55 PM
 * @ClassName DriverCertificateCameraActivity
 * <p>
 * Desc :
 */
public class DriverCertificateCameraActivity extends AppCompatActivity implements View.OnTouchListener, CameraPresenter.CameraCallBack {
    private AutoFitSurfaceView mSurfaceView;
    private ImageView mIvBack, mIvBut;
    /**
     * 驾照正面提示
     */
    private ImageView iconDrivingFront;
    /**
     * 身份证反面提示
     */
    private ImageView iconIdentityContrary;
    /**
     * 身份证正面提示
     */
    private ImageView iconIdentityFront;
    private TextView tvCameraOk, tvCameraCancel;
    private ImageView ivPreview;
    private CameraPresenter mCameraPresenter;
    private int mode = 0;
    private float startDis;
    private static final int MODE_INIT = 0;
    private static final int MODE_ZOOM = 1;
    private boolean isMove = false;
    private int mPictureType = -1;
    private OlaCameraMedia media;
    private LinearLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        layout = findViewById(R.id.layout_horizontal);
        mSurfaceView = findViewById(R.id.sf_camera);
        mIvBack = findViewById(R.id.iv_camera_back);
        mIvBut = findViewById(R.id.iv_camera_but);
        ivPreview = findViewById(R.id.iv_camera_preview);
        iconDrivingFront = findViewById(R.id.iv_camera_driving_front);
        iconIdentityContrary = findViewById(R.id.iv_camera_identity_contrary);
        iconIdentityFront = findViewById(R.id.iv_camera_identity_front);
        tvCameraOk = findViewById(R.id.tv_camera_ok);
        tvCameraCancel = findViewById(R.id.tv_camera_cancel);
        mCameraPresenter = new CameraPresenter(this, mSurfaceView);
        //设置后置摄像头
        mCameraPresenter.setFrontOrBack(Camera.CameraInfo.CAMERA_FACING_BACK);
        rotateLayout();
        getIntentData();
        initView();
        initListener();
    }

    /**
     * 旋转屏幕
     */
    private void rotateLayout() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = BottomStatusUtils.getDpi(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(height, width));
        layout.setRotation(90);
        layout.setY((height - width) / 2);
        layout.setX((width - height) / 2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPresenter != null) {
            mCameraPresenter.releaseCamera();
        }
    }

    private void initView() {

        switch (mPictureType) {
            case CameraConstant.IDENTITY_CARD_FRONT:
                iconIdentityFront.setVisibility(View.VISIBLE);
                break;
            case CameraConstant.IDENTITY_CARD_REVERSE:
                iconIdentityContrary.setVisibility(View.VISIBLE);
                break;
            case CameraConstant.DRIVING_LICENCE_FRONT:
                iconDrivingFront.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mPictureType = getIntent().getExtras().getInt(CameraConstant.DRIVER_INFO_PICTURE_HINT_TYPE);
        }
    }

    private void initListener() {
        //添加监听
        mCameraPresenter.setCameraCallBack(this);
        mSurfaceView.setOnTouchListener(this);
        mIvBut.setOnTouchListener(this);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnImgPath();
            }
        });
        mIvBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraPresenter.takePicture();
            }
        });
        tvCameraOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnImgPath();
            }
        });
        tvCameraCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePreview(media != null ? media.getPath() : "");
            }
        });
    }


    private void showPreview(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty() && !isDestroyed()) {
            Glide.with(this).load(imagePath).into(ivPreview);
            ivPreview.setVisibility(View.VISIBLE);
            mSurfaceView.setVisibility(View.GONE);
            mIvBut.setVisibility(View.GONE);
            mIvBack.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(tvCameraOk, "translationY", 0, -200).setDuration(300).start();
            ObjectAnimator.ofFloat(tvCameraCancel, "translationY", 0, 200).setDuration(300).start();
            tvCameraOk.setVisibility(View.VISIBLE);
            tvCameraCancel.setVisibility(View.VISIBLE);
        }
    }

    private void hidePreview(String imagePath) {
        if (ivPreview.getVisibility() == View.VISIBLE) {
            ivPreview.setVisibility(View.GONE);
            mSurfaceView.setVisibility(View.VISIBLE);
            mIvBut.setVisibility(View.VISIBLE);
            mIvBack.setVisibility(View.VISIBLE);
            tvCameraOk.setVisibility(View.GONE);
            tvCameraCancel.setVisibility(View.GONE);
        }
        if (imagePath != null && !imagePath.isEmpty()) {
            deleteSingleFile(imagePath);
        }
    }

    private void deleteSingleFile(String filePathName) {
        File file = new File(filePathName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 触摸回调
     *
     * @param v     添加Touch事件具体的view
     * @param event 具体事件
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //无论多少跟手指加进来，都是MotionEvent.ACTION_DWON MotionEvent.ACTION_POINTER_DOWN
        //MotionEvent.ACTION_MOVE:
        if (v.getId() == R.id.sf_camera) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                //手指按下屏幕
                case MotionEvent.ACTION_DOWN:
                    mode = MODE_INIT;
                    break;
                //当屏幕上已经有触摸点按下的状态的时候，再有新的触摸点被按下时会触发
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;
                    //计算两个手指的距离 两点的距离
                    startDis = twoPointDistance(event);
                    break;
                //移动的时候回调
                case MotionEvent.ACTION_MOVE:
                    isMove = true;
                    //这里主要判断有两个触摸点的时候才触发
                    if (mode == MODE_ZOOM) {
                        //只有两个点同时触屏才执行
                        if (event.getPointerCount() < 2) {
                            return true;
                        }
                        //获取结束的距离
                        float endDis = twoPointDistance(event);
                        //每变化10f zoom变1
                        int scale = (int) ((endDis - startDis) / 10f);
                        if (scale >= 1 || scale <= -1) {
                            int zoom = mCameraPresenter.getZoom() + scale;
                            //判断zoom是否超出变焦距离
                            if (zoom > mCameraPresenter.getMaxZoom()) {
                                zoom = mCameraPresenter.getMaxZoom();
                            }
                            //如果系数小于0
                            if (zoom < 0) {
                                zoom = 0;
                            }
                            //设置焦距
                            mCameraPresenter.setZoom(zoom);
                            //将最后一次的距离设为当前距离
                            startDis = endDis;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //判断是否点击屏幕 如果是自动聚焦
                    if (isMove == false) {
                        //自动聚焦
                        mCameraPresenter.autoFoucus();
                    }
                    isMove = false;
                    break;
                default:
                    break;
            }
            return true;
        }
        if (v.getId() == R.id.iv_camera_but) {
            //点击拍照后的缩放效果
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIvBut.setScaleX((float) 0.95);
                    mIvBut.setScaleY((float) 0.95);
                    break;

                case MotionEvent.ACTION_UP:
                    mIvBut.setScaleX(1);
                    mIvBut.setScaleY(1);
                    break;
                default:
            }
            return false;
        }
        return true;
    }

    /**
     * 两点的距离
     *
     * @param event 事件
     * @return
     */
    public float twoPointDistance(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 返回预览数据
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    /**
     * 返回拍照数据
     *
     * @param data
     * @param Camera
     */
    @Override
    public void onTakePicture(byte[] data, Camera Camera) {

    }

    /**
     * 返回图片路径
     *
     * @param media
     */
    @Override
    public void getPhotoFile(OlaCameraMedia media) {
        if (media != null) {
            showPreview(media.getPath());
            this.media = media;
        }
    }

    @Override
    public void onBackPressed() {
        returnImgPath();
    }

    public void returnImgPath() {
        Intent intent = new Intent();
        intent.putExtra(CameraConstant.RESULT_IMG_PATH, media);
        setResult(RESULT_OK, intent);
        finish();
    }
}
