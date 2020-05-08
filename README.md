# common_camera

司机端自定义相机组件

### 接入

1. #### 根目录build.gradle添加

   ```groovy
   repositories {
       maven {
           url 'https://nexus.olafuwu.com/repository/maven-oleyc-android-releases/'
           credentials {
               username rootProject.ext.maven["username"]
               password rootProject.ext.maven["userpassword"]
           }
       }
   }
   ```

2. #### 添加依赖

   ```groovy
   api 'com.ole.travel:camera:1.2.8'
   ```

3. #### manifest添加权限

   ```xml
   <uses-permission android:name="android.permission.CAMERA" />
   <uses-feature android:name="android.hardware.camera" />
   <uses-feature android:name="android.hardware.camera.autofocus" />
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   ```

4. #### 初始化

   ```java
   //初始化图片缓存地址，如果未进行初始化默认会保存在data/data/PackageName/pic/
   CameraHelper.getInstance(this).init(this.getFilesDir().getPath() + "pic/");
   ```

### 使用方式

1. 跳转拍照提示

   ##### 请先判断App是否有拍照与存储权限！！

   ```java
   Intent intent = new Intent(MainActivity.this, DriverInfoPictureHintActivity.class);
   //传参内容为证件种类，具体内容详见参数说明
   intent.putExtra(CameraConstant.DRIVER_INFO_PICTURE_HINT_TYPE, CameraConstant.DRIVING_LICENCE_FRONT);
   startActivityForResult(intent, CameraConstant.REQUEST_CODE_PICTURE);
   ```

2. 结果回调

   ##### 请判断返回的OlaCameraMedia是否为空
   
   ```java
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == CameraConstant.REQUEST_CODE_PICTURE) {
           if (data != null && data.getParcelableExtra(CameraConstant.RESULT_PATH_FLAG) != null) {
               OlaCameraMedia media= data.getParcelableExtra(CameraConstant.RESULT_PATH_FLAG);
               Log.e("zz","收到图片原始地址："+media.getPath());
               Log.e("zz","收到图片名称："+media.getFileName());
               Log.e("zz","收到图片AndroidQ：地址"+media.getAndroidQToPath());
               Glide.with(this).load(media.getPath()).into(mImg);
           }
       }
   }
   ```
   
3. 返回值OlaCameraMedia说明

   ```java
   media.getPath()//获取图片原始地址
   media.getFileName()//获取图片文件名称
   media.getAndroidQToPath()//获取图片AndroidQ的图片地址
   ```

### 参数说明

说明：跳转时传参内容

```java
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
```

