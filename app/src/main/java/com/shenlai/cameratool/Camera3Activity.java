package com.shenlai.cameratool;
//Camera3Activity

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.Collections;

public class Camera3Activity extends AppCompatActivity {
    private final String TAG = "CameraActivity";

    SurfaceView mCamera0;
    SurfaceView mCamera1;
    private SurfaceHolder mCam0_SurfaceHolder, mCam1_SurfaceHolder;

    private CameraManager mCameraManager;//摄像头管理器
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;

    TextView mTextViewNumsCamera;
    private static int nums_camera = 0;
    private Button btexit;
    private HandlerThread mCameraThread0 ,mCameraThread1;
    private Handler mCameraHandler0 ,mCameraHandler1;
    private CameraCharacteristics mCameraCharacteristics;
    private  Range<Integer>[] fpsRanges;
    private Range<Integer> fpsRange1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera3);
        fpsRange1 = Range.create(5, 8);
        initCameraActivity();
    }

    @SuppressLint("SetTextI18n")
    private void initCameraActivity(){
        mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);

        mCameraThread0 = new HandlerThread("mCameraThread0");
        mCameraThread0.start();

        mCameraHandler0 = new Handler(mCameraThread0.getLooper());
        mCameraThread1 = new HandlerThread("mCameraThread1");
        mCameraThread1.start();
        mCameraHandler1 = new Handler(mCameraThread1.getLooper());
        try {
            nums_camera = mCameraManager.getCameraIdList().length;
            Log.d(TAG, "The number of camera is " + nums_camera);
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
        mTextViewNumsCamera = (TextView)findViewById(R.id.nums_cammera);
        mTextViewNumsCamera.setText(""+nums_camera);
        btexit = (Button) findViewById(R.id.exit);
        btexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
        mCamera0 = (SurfaceView)findViewById(R.id.surface_view_camera0);
        mCam0_SurfaceHolder = mCamera0.getHolder();
        mCam0_SurfaceHolder.setKeepScreenOn(true);
        mCam0_SurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                setupCamera0("0");
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        if(nums_camera <= 0) mCamera0.setVisibility(View.GONE);

        mCamera1 = (SurfaceView)findViewById(R.id.surface_view_camera1);
        mCam1_SurfaceHolder = mCamera1.getHolder();
        mCam1_SurfaceHolder.setKeepScreenOn(true);
        mCam1_SurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                setupCamera1("1");
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                surfaceHolder = null;
            }
        });

    }
    private void takePreview0(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "takePreview2...");
        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            //   previewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange1);
            Log.e(TAG,"fpsRange1-----" + fpsRange1);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        //previewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange1);
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, mCameraHandler0);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.e(TAG, "config camera Error!!!");
                }
            }, mCameraHandler0);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void takePreview1(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "takePreview1...");
        try {
            // 创建预览需要的CaptureRequest.Builder
            Log.d(TAG, "takePreview2...");
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            //    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange1);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Collections.singletonList(surfaceHolder.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    Log.d(TAG, "takePreview3...");
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        Log.d(TAG, "takePreview4...");

                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        //  previewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange1);
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, mCameraHandler1);
                        Log.d(TAG, "takePreview5...");
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.e(TAG, "config camera Error!!!");
                }
            }, mCameraHandler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void setupCamera0(String cameraId) {
        //获取摄像头管理
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//         mCameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
//            fpsRanges =
//                    mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
//            Log.e(TAG,"fpsRanges" + Arrays.toString(fpsRanges) );
//
//        }catch (CameraAccessException e){
//            e.printStackTrace();
//        }
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                Log.e(TAG, "No camera permission!!!");
                return;
            } else {
                //打开摄像头
                mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {//打开摄像头
                        mCameraDevice = camera;
                        Log.d(TAG, "camera.getId()=" + camera.getId());
                        SurfaceHolder surfaceHolder = null;
                        //开启预览
                        if (camera.getId() == "0") {
                            surfaceHolder = mCam0_SurfaceHolder;
                            takePreview0(surfaceHolder);
                        }
                    }


                    @Override
                    public void onDisconnected(CameraDevice camera) {//关闭摄像头
                        camera.close();
                        if (null != mCameraDevice) {
                            mCameraDevice = null;
                        }
                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {//发生错误
                        camera.close();
                        Log.e(TAG, "CameraDevice.StateCallback() onError! error=" + error);
                    }
                }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupCamera1(String cameraId) {
        //获取摄像头管理
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
//            Range<Integer>[] fpsRanges =
//                    mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
//            Log.e(TAG,"fpsRanges" + Arrays.toString(fpsRanges));
//        }catch (CameraAccessException e){
//            e.printStackTrace();
//        }
        try {
            Log.e(TAG,"---------------------1");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                Log.e(TAG, "No camera permission!!!");
                return;
            } else {
                //打开摄像头
                mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {//打开摄像头
                        mCameraDevice = camera;
                        Log.d(TAG, "camera.getId()=" + camera.getId());
                        SurfaceHolder surfaceHolder = null;
                        //开启预览
                        if(camera.getId() == "0") {
                            surfaceHolder = mCam0_SurfaceHolder;
                        } else if(camera.getId() == "1") {
                            surfaceHolder = mCam1_SurfaceHolder;
                        }
                        Log.e(TAG,"---------------------2");
                        if(surfaceHolder != null) {
                            Log.e(TAG,"---------------------3");
                            takePreview1(surfaceHolder);
                        }
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {//关闭摄像头
                        camera.close();
                        if (null != mCameraDevice) {
                            mCameraDevice = null;
                        }
                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {//发生错误
                        camera.close();
                        Log.e(TAG, "CameraDevice.StateCallback() onError! error=" + error);
                    }
                }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }
}
