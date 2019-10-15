package com.shenlai.cameratool

import android.app.Activity
import android.graphics.ImageFormat
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.arcsoft.facetracking.AFT_FSDKFace
import kotlinx.android.synthetic.main.activity_camera4.*

class Camera4Activity : Activity() {
    private var mCamera0: Camera? = null
    private var mCamera1: Camera? = null
    private lateinit var mCameraPreviewSize: Camera.Size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera4)
        window.attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        initCamera()
        mSurfaceView0.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.e("Holder0", "$width $height")
                Thread(Runnable {
                    mCamera0?.setPreviewDisplay(holder)
                    mCamera0?.startPreview()
                }).start()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {

            }
        })
        mSurfaceView1.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.e("Holder1", "$width $height")
//                mCamera1.setPreviewDisplay(holder)
//                mCamera1.startPreview()
                Thread(Runnable {
                    mCamera1?.setPreviewDisplay(holder)
                    mCamera1?.startPreview()
                }).start()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {

            }
        })
    }

    override fun onDestroy() {
        releaseCamera()
        super.onDestroy()
    }


    private fun initCamera() {
        mCamera0= Camera.open(0)
        mCamera1= Camera.open(1)

        //mCamera0 = this.getCamera(0)!!
        Thread(Runnable {
            setParameters(mCamera0)
            mCamera0!!.setPreviewCallback { bytes, _ ->
                Thread(Runnable { Log.e("setPreviewCallback0", "0") }).start()
                //Log.e("setPreviewCallback 0","0")
                //faceDetect(bytes,0)
            }
        }).start()

        //mCamera1 = this.getCamera(1)!!
        Thread(Runnable {
            setParameters(mCamera1)
            mCamera1!!.setPreviewCallback { bytes, _ ->
                //Thread(Runnable { Log.e("setPreviewCallback 1", "1") }).start()
                Log.e("setPreviewCallback1", "1")
                //faceDetect(bytes,1)
            }
        }).start()
    }

    private fun getCamera(id: Int): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return camera
    }

    private fun releaseCamera() {
        mCamera0?.setPreviewCallback(null)
        mCamera0?.stopPreview()
        mCamera0?.release()

        mCamera1?.setPreviewCallback(null)
        mCamera1?.stopPreview()
        mCamera1?.release()
    }

    private fun setParameters(camera: Camera?) {
        if (camera == null) {
            return
        }
        val parameters = camera.parameters
        val previewSize = parameters.supportedPreviewSizes.first()

        for ((idx, sz) in camera.parameters?.supportedPreviewSizes!!.withIndex()) {
            Log.e("supported preview size$idx", "${sz.width} ${sz.height}")
        }

        //parameters.setPreviewSize(previewSize.width, previewSize.height)
        parameters.setPreviewSize(640, 480)
        parameters.previewFormat = ImageFormat.NV21
        camera.parameters = parameters

        mCameraPreviewSize = camera.parameters.previewSize
        Log.e("preview size", "${mCameraPreviewSize.width} ${mCameraPreviewSize.height}")
        //resetSize()
    }

    private fun faceDetect(mImageNV21: ByteArray, id: Int) {
        Log.e("setPreviewCallback $id", "")
        val mFace: AFT_FSDKFace? = FaceUtil.getInstance()?.getFace(
            mImageNV21,
            mCamera0?.parameters?.previewSize?.width!!, mCamera0!!.parameters.previewSize.height
        )
            ?: return
        Log.e("face $id", mFace?.rect?.toShortString())
    }

//    private fun resetSize() {
//        //mSurfaceView0.layoutParams = LinearLayout.LayoutParams(mCameraPreviewSize.width, mCameraPreviewSize.height)
//        mSurfaceView0.layoutParams = LinearLayout.LayoutParams(640, 480)
//        mSurfaceView1.layoutParams = LinearLayout.LayoutParams(640, 480)
//    }
}
