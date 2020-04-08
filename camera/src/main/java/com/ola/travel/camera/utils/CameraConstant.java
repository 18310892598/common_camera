package com.ola.travel.camera.utils;

/**
 * @author zhangzheng
 * @Date 2020/4/2 3:44 PM
 * @ClassName Constant
 * <p>
 * Desc :
 */
public class CameraConstant {

    /**
     * 身份证正面
     */
    public static final int IDENTITY_CARD_FRONT = 1;
    /**
     * 身份证反面
     */
    public static final int IDENTITY_CARD_REVERSE = 2;
    /**
     * 手持身份证
     */
    public static final int IDENTITY_CARD_HAND = 3;
    /**
     * 驾照正面
     */
    public static final int DRIVING_LICENCE_FRONT = 4;
    /**
     * 驾照反面
     */
    public static final int DRIVING_LICENCE_REVERSE = 5;
    /**
     * 网约车证明
     */
    public static final int DRIVING_LICENCE_ONLINE = 6;

    public static final String DRIVER_INFO_PICTURE_HINT_TYPE = "driver_info_picture_hint_type";
    /**
     * 跳转
     */
    public static final int REQUEST_CODE_PICTURE = 10001;
    public static final int REQUEST_CODE_CAMERA = 10002;
    public static final int RESULT_CODE_PATH = 20002;

    public static final String RESULT_PATH_FLAG = "result_path_flag";
    public static final String RESULT_IMG_PATH = "result_img_path";

    /**
     * toast样式
     */
    public static int TOAST_FAIL = 1;
    public static int TOAST_SUCCESS = 2;
    public static int TOAST_LOADING = 3;
    public static int TOAST_WRONG = 4;
    public static int TOAST_NONE = 5;
}
