package com.shenlai.cameratool.camera2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import java.util.*


class Camera2Helper(
    context: Context,
    id: Int,
    textureView: TextureView,
    faceDetect: ImageCallback.FaceDetect
) {
    private var mContext: Context = context
    private var mCameraId: String = id.toString()
    private var mTextureView: TextureView = textureView
    private var mFaceDetectMode: Int = 0

    private lateinit var mPreviewSize: Size
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var mCameraCaptureSession: CameraCaptureSession

    private val mDebug by lazy {
        true
    }

    private val mImageReader: ImageReader by lazy {
        val imageReader: ImageReader = ImageReader.newInstance(mPreviewSize.width, mPreviewSize.height, ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener({ mCameraHandler.post(ImageCallback(it.acquireNextImage(), faceDetect)) }, mCameraHandler)
        imageReader
    }
    private val mCameraThread: HandlerThread by lazy {
        HandlerThread("Camera${mCameraId}Thread")
    }
    private val mCameraHandler: Handler by lazy {
        mCameraThread.start()
        log("mCameraHandler init.")
        Handler(mCameraThread.looper)
    }
    private val mCaptureRequestBuilder: CaptureRequest.Builder by lazy {
        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
    }
    private val mCaptureRequest: CaptureRequest by lazy {
        mCaptureRequestBuilder.build()
    }

    private val mCameraManager: CameraManager by lazy {
        mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val mSupportFaceDetector by lazy {
        val characteristics = mCameraManager.getCameraCharacteristics(mCameraId)
        val fd = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES)!!
        val maxFD = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT)!!

        var support = false
        if (fd.isNotEmpty()) {
            val fdList = ArrayList<Int>()
            for (FaceD in fd) {
                fdList.add(FaceD)
                log("setUpCameraOutputs: FD type:$FaceD")
            }
            log("setUpCameraOutputs: FD count$maxFD")

            if (maxFD > 0) {
                support = true
                mFaceDetectMode = Collections.max(fdList)
            }
        }
        support
    }

    init {
        log("camera$mCameraId init.")
    }

    private fun log(debug: String) {
        if (mDebug) Log.e("Camera2Helper camera$mCameraId", debug)
    }

    fun release() {
        mCameraCaptureSession.close()
        mCameraDevice.close()
        mImageReader.close()
    }

    fun start(): Camera2Helper {
        setTextureView()
        log("start.")
        return this
    }

    private fun setTextureView() {
        if (!mTextureView.isAvailable) {
            mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    setupCamera(width, height)
                    //setupCamera(width/height)
                    openCamera()
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

                }
            }
        } else {
            startPreview()
        }
    }

    private fun setupCamera(width: Int, height: Int) {
        try {
            //遍历所有摄像头
            val list = mCameraManager.cameraIdList
            for (cameraId in list) {
                log(cameraId)
            }
            val characteristics = mCameraManager.getCameraCharacteristics(mCameraId)

            //0为front 1为back
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            log("facing:$facing")

            //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            log(map.toString())
            val t = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES)!!
            log(t.toString())

            //根据TextureView的尺寸设置预览尺寸
            //mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
            mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture::class.java), width, height)
            mPreviewSize = Size(640, 480)
            log(mPreviewSize.toString())
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    //选择sizeMap中大于并且最接近width和height的size
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size {
        val sizeList = ArrayList<Size>()
        for (option in sizeMap) {
            if (width > height) {
                if (option.width > width && option.height > height) {
                    sizeList.add(option)
                }
            } else {
                if (option.width > height && option.height > width) {
                    sizeList.add(option)
                }
            }
        }
        return if (sizeList.size > 0) {
            Collections.min(sizeList) { lhs, rhs -> java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong()) }
        } else sizeMap[0]
    }

    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mCameraManager.openCamera(mCameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    mCameraDevice = camera
                    startPreview()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                }
            }, mCameraHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun startPreview() {
        val surfaceTexture = mTextureView.surfaceTexture
        surfaceTexture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
        val previewSurface = Surface(surfaceTexture)
        try {
            mCaptureRequestBuilder.addTarget(previewSurface)
            mCaptureRequestBuilder.addTarget(mImageReader.surface)
            if (mSupportFaceDetector) {
                mCaptureRequestBuilder.set(
                    CaptureRequest.STATISTICS_FACE_DETECT_MODE, mFaceDetectMode
                )
            }
            mCameraDevice.createCaptureSession(
                mutableListOf(previewSurface, mImageReader.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            mCameraCaptureSession = session
                            mCameraCaptureSession.setRepeatingRequest(
                                mCaptureRequest,
                                object : CameraCaptureSession.CaptureCallback() {
                                    override fun onCaptureCompleted(
                                        session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult
                                    ) {
                                        val face = result.get(CaptureResult.STATISTICS_FACES)

                                        if (face!!.isNotEmpty()) {
                                            log("face detected " + face.size.toString())
                                        }
                                    }
                                }
                                , mCameraHandler)
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {

                    }
                },
                mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}
