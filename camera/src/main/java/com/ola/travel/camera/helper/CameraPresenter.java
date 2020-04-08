package com.ola.travel.camera.helper;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ola.travel.camera.CameraHelper;
import com.ola.travel.camera.utils.CameraConstant;
import com.ola.travel.camera.utils.ThreadPoolManager;
import com.ola.travel.camera.view.AutoFitSurfaceView;
import com.ole.libtoast.OlaToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author created by knight
 * @organize
 * @Date 2019/9/24 18:29
 * @descript:
 */

public class CameraPresenter implements Camera.PreviewCallback {
    /**
     * 相机对象
     */
    private Camera mCamera;
    /**
     * 相机对象参数设置
     */
    private Camera.Parameters mParameters;
    /**
     * 自定义照相机页面
     */
    private AppCompatActivity mAppCompatActivity;
    /**
     * surfaceView 用于预览对象
     */
    private AutoFitSurfaceView mSurfaceView;
    /**
     * SurfaceHolder对象
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * 摄像头Id 默认后置 0,前置的值是1
     */
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 预览旋转的角度
     */
    private int orientation;
    /**
     * 自定义回调
     */
    private CameraCallBack mCameraCallBack;
    /**
     * 手机宽和高
     */
    private int screenWidth, screenHeight;
    /**
     * 拍照数量
     */
    private int photoNum = 0;
    /**
     * 拍照存放的文件
     */
    private File photosFile = null;
    /**
     * 当前缩放具体值
     */
    private int mZoom;

    /**
     * 存储图片的线程
     */
    private ScheduledFuture scheduledFuture;


    //自定义回调
    public interface CameraCallBack {
        //预览帧回调
        void onPreviewFrame(byte[] data, Camera camera);

        //拍照回调
        void onTakePicture(byte[] data, Camera Camera);

        //拍照路径返回
        void getPhotoFile(String imagePath);
    }

    public void setCameraCallBack(CameraCallBack mCameraCallBack) {
        this.mCameraCallBack = mCameraCallBack;

    }

    public CameraPresenter(AppCompatActivity mAppCompatActivity, AutoFitSurfaceView mSurfaceView) {
        this.mAppCompatActivity = mAppCompatActivity;
        this.mSurfaceView = mSurfaceView;
        mSurfaceHolder = mSurfaceView.getHolder();
        DisplayMetrics dm = new DisplayMetrics();
        mAppCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //获取宽高像素
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        Log.d("sssd-手机宽高尺寸:", screenWidth + "*" + screenHeight);
        //创建文件夹目录
        setUpFile();
        init();
    }


    /**
     * 设置前置还是后置
     *
     * @param mCameraId 前置还是后置
     */
    public void setFrontOrBack(int mCameraId) {
        this.mCameraId = mCameraId;

    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (mCamera != null) {
            //拍照回调 点击拍照时回调 写一个空实现
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, new Camera.PictureCallback() {
                //回调没压缩的原始数据
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                }
            }, new Camera.PictureCallback() {
                //回调图片数据 点击拍照后相机返回的照片byte数组，照片数据
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //拍照后记得调用预览方法，不然会停在拍照图像的界面
                    mCamera.startPreview();
                    //回调
                    mCameraCallBack.onTakePicture(data, camera);
                    //保存图片
                    getPhotoPath(data);

                }
            });

        }
    }

    /**
     * 初始化增加回调
     */
    public void init() {
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //surface创建时执行
                if (mCamera == null) {
                    openCamera(mCameraId);
                }
                //并设置预览
                startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //surface绘制时执行
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //surface销毁时执行
                releaseCamera();
            }
        });
    }

    /**
     * 打开相机 并且判断是否支持该摄像头
     *
     * @param FaceOrBack 前置还是后置
     * @return
     */
    private boolean openCamera(int FaceOrBack) {
        //是否支持前后摄像头
        boolean isSupportCamera = isSupport(FaceOrBack);
        //如果支持
        if (isSupportCamera) {
            try {
                mCamera = Camera.open(FaceOrBack);
                initParameters(mCamera);
                //设置预览回调
                if (mCamera != null) {
                    mCamera.setPreviewCallback(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OlaToast.show(mAppCompatActivity, "请检查您的相机是否正常", CameraConstant.TOAST_FAIL);
                return false;
            }

        }

        return isSupportCamera;
    }


    /**
     * 设置相机参数
     *
     * @param camera
     */
    private void initParameters(Camera camera) {
        try {
            //获取Parameters对象
            mParameters = camera.getParameters();
            //设置预览格式
            mParameters.setPreviewFormat(ImageFormat.NV21);
            //mParameters.setExposureCompensation(2);
            setPreviewSize(screenWidth, screenHeight);
            setPictureSize();
            //连续自动对焦图像
            if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (isSupportFocus(Camera.Parameters.FOCUS_MODE_AUTO)) {
                //自动对焦(单次)
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }

            //给相机设置参数
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
            OlaToast.show(mAppCompatActivity, "初始化相机失败", CameraConstant.TOAST_FAIL);
        }


    }


    /**
     * 设置保存图片的尺寸
     */
    private void setPictureSize() {
        List<Camera.Size> localSizes = mParameters.getSupportedPictureSizes();
        Camera.Size biggestSize = null;
        Camera.Size fitSize = null;// 优先选预览界面的尺寸
        Camera.Size previewSize = mParameters.getPreviewSize();//获取预览界面尺寸
        float previewSizeScale = 0;
        if (previewSize != null) {
            previewSizeScale = previewSize.width / (float) previewSize.height;
        }

        if (localSizes != null) {
            int cameraSizeLength = localSizes.size();
            for (int n = 0; n < cameraSizeLength; n++) {
                Camera.Size size = localSizes.get(n);
                if (biggestSize == null) {
                    biggestSize = size;
                } else if (size.width >= biggestSize.width && size.height >= biggestSize.height) {
                    biggestSize = size;
                }

                // 选出与预览界面等比的最高分辨率
                if (previewSizeScale > 0
                        && size.width >= previewSize.width && size.height >= previewSize.height) {
                    float sizeScale = size.width / (float) size.height;
                    if (sizeScale == previewSizeScale) {
                        if (fitSize == null) {
                            fitSize = size;
                        } else if (size.width >= fitSize.width && size.height >= fitSize.height) {
                            fitSize = size;
                        }
                    }
                }
            }

            // 如果没有选出fitSize, 那么最大的Size就是FitSize
            if (fitSize == null) {
                fitSize = biggestSize;
            }
            mParameters.setPictureSize(fitSize.width, fitSize.height);
        }

    }

    /**
     * 设置预览界面尺寸
     */
    public void setPreviewSize(int width, int height) {
        Log.e("zz","屏幕分辨率："+width+"*"+height);
        //获取系统支持预览大小
        List<Camera.Size> localSizes = mParameters.getSupportedPreviewSizes();
        //最大分辨率
        Camera.Size biggestSize = null;
        Camera.Size fitSize = getOptimalSize(localSizes, width, height);
        float scale = Float.valueOf(width) / height;
        if (localSizes != null) {
            int cameraSizeLength = localSizes.size();
            for (int n = 0; n < cameraSizeLength; n++) {
                Camera.Size size = localSizes.get(n);
                Log.e("zz","预置分辨率："+size.width+"*"+size.height);
                if (Float.valueOf(size.width) / size.height == scale) {
                    mParameters.setPreviewSize(size.width, size.height);
                    return;
                } else {
                    if (biggestSize == null ||
                            (size.width >= biggestSize.width && size.height >= biggestSize.height)) {
                        biggestSize = size;
                    }
                }
            }
            if (biggestSize.width > width) {
                mParameters.setPreviewSize(biggestSize.width, biggestSize.height);
                return;
            } else {
                mSurfaceHolder.setFixedSize(fitSize.width, fitSize.height);
                mSurfaceView.setAspectRatio(fitSize.width, fitSize.height);
            }
        }
    }

    private static Camera.Size getOptimalSize(@NonNull List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.e("zz","最佳分辨率："+optimalSize.width+"*"+optimalSize.height);
        return optimalSize;
    }

    /**
     * 变焦
     *
     * @param zoom 缩放系数
     */
    public void setZoom(int zoom) {
        if (mCamera == null) {
            return;
        }
        //获取Paramters对象
        Camera.Parameters parameters;
        parameters = mCamera.getParameters();
        //如果不支持变焦
        if (!parameters.isZoomSupported()) {
            return;
        }
        //
        parameters.setZoom(zoom);
        //Camera对象重新设置Paramters对象参数
        mCamera.setParameters(parameters);
        mZoom = zoom;

    }


    /**
     * 返回缩放值
     *
     * @return 返回缩放值
     */
    public int getZoom() {
        return mZoom;
    }


    /**
     * 获取最大Zoom值
     *
     * @return zoom
     */
    public int getMaxZoom() {
        if (mCamera == null) {
            return -1;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (!parameters.isZoomSupported()) {
            return -1;
        }
        return parameters.getMaxZoom() > 50 ? 50 : parameters.getMaxZoom();
    }


    /**
     * 判断是否支持对焦模式
     *
     * @return
     */
    private boolean isSupportFocus(String focusMode) {
        boolean isSupport = false;
        //获取所支持对焦模式
        List<String> listFocus = mParameters.getSupportedFocusModes();
        for (String s : listFocus) {
            //如果存在 返回true
            if (s.equals(focusMode)) {
                isSupport = true;
            }

        }
        return isSupport;
    }


    /**
     * 判断是否支持某个相机
     *
     * @param faceOrBack 前置还是后置
     * @return
     */
    private boolean isSupport(int faceOrBack) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            //返回相机信息
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == faceOrBack) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mCameraCallBack != null) {
            mCameraCallBack.onPreviewFrame(data, camera);
        }
    }

    /**
     * 开始预览
     */
    private void startPreview() {
        try {
            //根据所传入的SurfaceHolder对象来设置实时预览
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //调整预览角度
            setCameraDisplayOrientation(mAppCompatActivity, mCameraId, mCamera);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保证预览方向正确
     *
     * @param appCompatActivity Activity
     * @param cameraId          相机Id
     * @param camera            相机
     */
    private void setCameraDisplayOrientation(AppCompatActivity appCompatActivity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        //rotation是预览Window的旋转方向，对于手机而言，当在清单文件设置Activity的screenOrientation="portait"时，
        //rotation=0，这时候没有旋转，当screenOrientation="landScape"时，rotation=1。
        int rotation = appCompatActivity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Log.e("zz", "rotation:" + rotation);
        int result;
        //计算图像所要旋转的角度
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        orientation = result;
        //调整预览图像旋转角度
        camera.setDisplayOrientation(result);

    }

    /**
     * 前后摄像切换
     */
    public void switchCamera() {
        //先释放资源
        releaseCamera();
        //在Android P之前 Android设备仍然最多只有前后两个摄像头，在Android p后支持多个摄像头 用户想打开哪个就打开哪个
        mCameraId = (mCameraId + 1) % Camera.getNumberOfCameras();
        //打开摄像头
        openCamera(mCameraId);
        //切换摄像头之后开启预览
        startPreview();
    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            //停止预览
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mHandler.removeMessages(1);
        }
        if (scheduledFuture != null) {
            ThreadPoolManager.getInstance().cancelSingleThread(scheduledFuture);
        }
    }

    /**
     * 创建拍照照片文件夹
     */
    private void setUpFile() {
        //--todo--android 10适配
        photosFile = new File(CameraHelper.getInstance(mAppCompatActivity).getImgLocation());
        if (!photosFile.exists() || !photosFile.isDirectory()) {
            boolean isSuccess = false;
            try {
                isSuccess = photosFile.mkdirs();
            } catch (Exception e) {
                OlaToast.show(mAppCompatActivity, "创建存放目录失败,请检查磁盘空间", CameraConstant.TOAST_FAIL);
                mAppCompatActivity.finish();
            } finally {
                if (!isSuccess) {
                    OlaToast.show(mAppCompatActivity, "创建存放目录失败,请检查磁盘空间", CameraConstant.TOAST_FAIL);
                    mAppCompatActivity.finish();
                }
            }
        }
    }

    /**
     * @return 返回路径
     */
    private void getPhotoPath(final byte[] data) {
        scheduledFuture = ThreadPoolManager.getInstance().addDelayScheduledExecutor(new Runnable() {
            @Override
            public void run() {
                long timeMillis = System.currentTimeMillis();
                //拍照数量+1
                photoNum++;
                //图片名字
                String name = ("Driver_Picture_" + timeMillis + ".jpg");
                //创建具体文件
                File file = new File(photosFile, name);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    try {
                        //将数据写入文件
                        fos.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //--todo--
                    //将图片旋转
                    rotateImageView(mCameraId, orientation, CameraHelper.getInstance(mAppCompatActivity).getImgLocation() + file.getName());
                    Message message = new Message();
                    message.what = 1;
                    message.obj = CameraHelper.getInstance(mAppCompatActivity).getImgLocation() + file.getName();
                    mHandler.sendMessage(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 10, TimeUnit.MILLISECONDS);
    }


    /**
     * 旋转图片
     *
     * @param cameraId    前置还是后置
     * @param orientation 拍照时传感器方向
     * @param path        图片路径
     */
    private void rotateImageView(int cameraId, int orientation, String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Matrix matrix = new Matrix();
        // 创建新的图片
        Bitmap resizedBitmap;
        //0是后置
        if (cameraId == 0) {
            if (orientation == 90) {
                matrix.postRotate(90);
            }
        }
        //1是前置
        if (cameraId == 1) {
            matrix.postRotate(270);
        }
        // 创建新的图片
        resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //新增 如果是前置 需要镜面翻转处理
        if (cameraId == 1) {
            Matrix matrix1 = new Matrix();
            matrix1.postScale(-1f, 1f);
            resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0,
                    resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix1, true);

        }


        File file = new File(path);
        //重新写入文件
        try {
            // 写入文件
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            //默认jpg
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            resizedBitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mCameraCallBack.getPhotoFile(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 自动变焦
     */
    public void autoFoucus() {
        if (mCamera == null) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    }


    /**
     * 闪光灯
     *
     * @param turnSwitch true 为开启 false 为关闭
     */
    public void turnLight(boolean turnSwitch) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }

        parameters.setFlashMode(turnSwitch ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

    /**
     * @param mSurfaceView
     */
    public void update(SurfaceView mSurfaceView) {
        mSurfaceHolder = mSurfaceView.getHolder();

    }


}
