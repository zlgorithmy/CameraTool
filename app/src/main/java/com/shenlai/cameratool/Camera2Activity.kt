package com.shenlai.cameratool

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.shenlai.cameratool.camera2.Camera2Helper
import com.shenlai.cameratool.camera2.ImageCallback.FaceDetect
import kotlinx.android.synthetic.main.activity_camera2.*

class Camera2Activity : Activity() {
    private val mDebug by lazy {
        true
    }

    private fun log(debug: String) {
        if (mDebug) Log.e("Camera2Activity", debug)
    }

    private val mCameraFront: Camera2Helper by lazy {
        Camera2Helper(this, 0, mTextureView0, object : FaceDetect {
            override fun onNoFace() {
                log("camera0 detect no face")
            }

            override fun onFace() {
                log("camera0 detect no face")
            }
        })
    }
    private val mCameraBack: Camera2Helper by lazy {
        Camera2Helper(this, 1, mTextureView1, object : FaceDetect {
            override fun onNoFace() {
                log("camera1 detect no face")
            }

            override fun onFace() {
                log("camera1 detect no face")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //全屏无状态栏
        window.attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        setContentView(R.layout.activity_camera2)
    }

    override fun onResume() {
        super.onResume()
        mCameraFront.start()
        mCameraBack.start()
    }

    override fun onPause() {
        mCameraFront.release()
        mCameraBack.release()
        super.onPause()
    }
}