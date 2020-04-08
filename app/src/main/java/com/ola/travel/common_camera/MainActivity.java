package com.ola.travel.common_camera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ola.travel.camera.activity.DriverInfoPictureHintActivity;
import com.ola.travel.camera.utils.CameraConstant;

public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private ImageView mImg;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.main_but);
        mImg = findViewById(R.id.main_img);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverInfoPictureHintActivity.class);
                intent.putExtra(CameraConstant.DRIVER_INFO_PICTURE_HINT_TYPE, CameraConstant.DRIVING_LICENCE_FRONT);
                startActivityForResult(intent, CameraConstant.REQUEST_CODE_PICTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraConstant.REQUEST_CODE_PICTURE) {
            if (data != null && data.getStringExtra(CameraConstant.RESULT_PATH_FLAG) != null) {
                imagePath = data.getStringExtra(CameraConstant.RESULT_PATH_FLAG);
                Glide.with(this).load(imagePath).into(mImg);
            }
        }
    }
}
