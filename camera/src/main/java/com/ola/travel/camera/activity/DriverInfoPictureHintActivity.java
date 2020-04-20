package com.ola.travel.camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ola.travel.camera.bean.OlaCameraMedia;
import com.ola.travel.camera.utils.BottomStatusUtils;
import com.ola.travel.camera.utils.CameraConstant;
import com.ola.travel.camera.R;

import java.io.File;

/**
 * @author zhangzheng
 * @Date 2020/3/25 6:00 PM
 * @ClassName DriverInfoPictureHintActivity
 * <p>
 * Desc :司机拍照提示页面
 */
public class DriverInfoPictureHintActivity extends AppCompatActivity {
    private ImageView ivPictureHint;
    private TextView tvHintBut, tvHintText;
    private LinearLayout layout;
    private int mPictureType = -1;
    private OlaCameraMedia media;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏尺寸
        setContentView(R.layout.activity_recruit_picture_hint);
        layout = findViewById(R.id.layout_horizontal);
        ivPictureHint = findViewById(R.id.iv_picture_hint);
        tvHintBut = findViewById(R.id.tv_picture_hint_but);
        tvHintText = findViewById(R.id.iv_picture_hint_text_content);
        rotateLayout();
        getIntentData();
        initView();
    }

    /**
     * 旋转屏幕
     */
    private void rotateLayout() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels - BottomStatusUtils.getBottomStatusHeight(this);
        int h2 = BottomStatusUtils.getDpi(this);
        getWindow().setLayout(width, h2);
        layout.setLayoutParams(new LinearLayout.LayoutParams(height, width));
        layout.setRotation(90);
        layout.setY((height - width) >> 1);
        layout.setX((width - height) >> 1);
    }


    private void initView() {
        switch (mPictureType) {
            case CameraConstant.IDENTITY_CARD_FRONT:
                Glide.with(this)
                        .load(R.mipmap.icon_identity_card_front)
                        .dontAnimate()
                        .centerCrop()
                        .into(ivPictureHint);
                break;
            case CameraConstant.IDENTITY_CARD_REVERSE:
                Glide.with(this)
                        .load(R.mipmap.icon_identity_card_reverse)
                        .dontAnimate()
                        .centerCrop()
                        .into(ivPictureHint);
                break;
            case CameraConstant.IDENTITY_CARD_HAND:
                Glide.with(this)
                        .load(R.mipmap.icon_identity_card_hand)
                        .dontAnimate()
                        .centerCrop()
                        .into(ivPictureHint);
                tvHintText.setText("1、请朋友或者亲人帮忙拍摄，将身份证置于胸前\n" +
                        "2、拍摄环境光线明亮适中，不要过亮\n" +
                        "3、拍摄时不要遮挡面部，确保身份证清晰");
                break;
            case CameraConstant.DRIVING_LICENCE_FRONT:
                Glide.with(this)
                        .load(R.mipmap.icon_driving_licence_front)
                        .dontAnimate()
                        .centerCrop()
                        .into(ivPictureHint);
                break;
            case CameraConstant.DRIVING_LICENCE_REVERSE:
                Glide.with(this)
                        .load(R.mipmap.icon_driving_licence_reverse)
                        .dontAnimate()
                        .centerCrop()
                        .into(ivPictureHint);
                break;
            case CameraConstant.DRIVING_LICENCE_ONLINE:
                Glide.with(this)
                        .load(R.mipmap.icon_driving_licence_online)
                        .dontAnimate()
                        .centerCrop()
                        .into(ivPictureHint);
                break;
            default:
                break;
        }

        tvHintBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverInfoPictureHintActivity.this, DriverCertificateCameraActivity.class);
                intent.putExtra(CameraConstant.DRIVER_INFO_PICTURE_HINT_TYPE, mPictureType);
                startActivityForResult(intent, CameraConstant.REQUEST_CODE_CAMERA);
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityFinish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CameraConstant.REQUEST_CODE_CAMERA:
                    if (data != null) {
                        media = data.getParcelableExtra(CameraConstant.RESULT_IMG_PATH);
                        disposeResultData(media);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mPictureType = getIntent().getExtras().getInt(CameraConstant.DRIVER_INFO_PICTURE_HINT_TYPE);
        }
    }

    private void disposeResultData(OlaCameraMedia olaCameraMedia) {
        resultImagePath(olaCameraMedia);
    }

    /**
     * 删除本地缓存图片
     *
     * @param filePathName
     */
    private void deleteSingleFile(String filePathName) {
        File file = new File(filePathName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 返回图片地址
     */
    private void resultImagePath(OlaCameraMedia olaCameraMedia) {
        Intent intent = new Intent();
        intent.putExtra(CameraConstant.RESULT_PATH_FLAG, olaCameraMedia);
        setResult(CameraConstant.RESULT_CODE_PATH, intent);
        activityFinish();
    }

    private void activityFinish() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        activityFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (media != null) {
            deleteSingleFile(media.getPath());
        }
    }
}
