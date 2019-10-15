package com.shenlai.cameratool

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import com.arcsoft.facetracking.AFT_FSDKFace
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    companion object {
        private const val REQUEST_PERMISSION_CODE: Int = 1
        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private lateinit var mCamera: Camera
    private lateinit var list: List<Int>
    private lateinit var mCameraPreviewSize: Camera.Size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        if (!checkRequiredPermissions()) {
            return
        }
        list = listOf(
            R.drawable.g01, R.drawable.g02, R.drawable.g03, R.drawable.g04, R.drawable.g05,
            R.drawable.g06, R.drawable.g07, R.drawable.g08, R.drawable.g09, R.drawable.g10,
            R.drawable.g11, R.drawable.g12, R.drawable.g13, R.drawable.g14, R.drawable.g15,
            R.drawable.g16, R.drawable.g17, R.drawable.g18, R.drawable.g19, R.drawable.g20,
            R.drawable.g21, R.drawable.g22, R.drawable.g23, R.drawable.g24, R.drawable.g25,
            R.drawable.g26, R.drawable.g27, R.drawable.g28, R.drawable.g29, R.drawable.g30,
            R.drawable.g31, R.drawable.g32, R.drawable.g33, R.drawable.g34, R.drawable.g35,
            R.drawable.g36, R.drawable.g37, R.drawable.g38, R.drawable.g39, R.drawable.g40,
            R.drawable.g41, R.drawable.g42, R.drawable.g43, R.drawable.g44, R.drawable.g45,
            R.drawable.g46, R.drawable.g47, R.drawable.g48, R.drawable.g49, R.drawable.g50,
            R.drawable.g51, R.drawable.g52, R.drawable.g53, R.drawable.g54, R.drawable.g55,
            R.drawable.g56, R.drawable.g57, R.drawable.g58, R.drawable.g59, R.drawable.g60,
            R.drawable.g61, R.drawable.g62, R.drawable.g63
        )
        initCamera()
        mSurfaceView0.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.e("Holder", "$width $height")
                mCamera.setPreviewDisplay(holder)
                mCamera.startPreview()
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
        mCamera = this.getCamera()!!
        setParameters(mCamera)
        mCamera.setPreviewCallback { bytes, _ ->
            faceDetect(bytes)
        }
    }

    private fun getCamera(): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return camera
    }

    private fun releaseCamera() {
        mCamera.setPreviewCallback(null)
        mCamera.stopPreview()
        mCamera.release()
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
        parameters.setPreviewSize(previewSize.width, previewSize.height)
        parameters.previewFormat = ImageFormat.NV21
        camera.parameters = parameters

        mCameraPreviewSize = camera.parameters.previewSize
        Log.e("preview size", "${mCameraPreviewSize.width} ${mCameraPreviewSize.height}")
        resetSize()
    }

    private fun faceDetect(mImageNV21: ByteArray) {
        val mFace: AFT_FSDKFace? = FaceUtil.getInstance()?.getFace(
            mImageNV21,
            mCamera.parameters?.previewSize?.width!!, mCamera.parameters.previewSize.height
        )
            ?: return
        Log.e("face", mFace?.rect?.toShortString())
        val r: Rect = mFace!!.rect
        Log.e("R", (r.centerX() / mCamera.parameters?.previewSize?.width!!).toString())
        val index = r.centerX() * list.size / mCamera.parameters?.previewSize?.width!!
        if (index in list.indices) {
            mImageView.setImageDrawable(getDrawable(list[index]))
            mTextView.text = "$index"
        }
    }

    private fun resetSize() {
        mSurfaceView0.layoutParams = LinearLayout.LayoutParams(mCameraPreviewSize.width, mCameraPreviewSize.height)
        //mSurfaceView.layoutParams = LinearLayout.LayoutParams(1280, 720)
    }

    /**
     * 判断我们需要的权限是否被授予，只要有一个没有授权，我们都会返回 false，并且进行权限申请操作。
     *
     * @return true 权限都被授权
     */
    private fun checkRequiredPermissions(): Boolean {
        val deniedPermissions = mutableListOf<String>()
        for (permission in REQUIRED_PERMISSIONS) {
            if (checkSelfPermission(this, permission) == PERMISSION_DENIED) {
                deniedPermissions.add(permission)
            }
        }
        if (deniedPermissions.isEmpty().not()) {
            requestPermissions(deniedPermissions.toTypedArray(), REQUEST_PERMISSION_CODE)
        }
        return deniedPermissions.isEmpty()
    }
}
