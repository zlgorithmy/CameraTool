package com.shenlai.cameratool.camera2

import android.media.Image
import android.util.Log
import com.shenlai.cameratool.FaceUtil
import com.shenlai.cameratool.Image2NV21
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class ImageCallback(private val mImage: Image, private var mFaceCallback: FaceDetect?) : Runnable {
    private val mDebug: Boolean by lazy {
        true
    }

    private fun log(debug: String) {
        if (mDebug) Log.e("ImageCallback", debug)
    }

    interface FaceDetect {
        fun onFace()
        fun onNoFace()
    }
    private val newThreadPool by lazy {
        Executors.newCachedThreadPool(object : ThreadFactory {
            var count = 0
            override fun newThread(r: Runnable?): Thread {
                val thread = Thread(r, "faceDetectThread${count++}")
                thread.setUncaughtExceptionHandler { t, e ->
                    log(t.name)
                    e.printStackTrace()
                }
                return thread
            }
        })
    }

    override fun run() {
        val bytes = Image2NV21.getDataFromImage(mImage)
        val w = mImage.width
        val h = mImage.height
        mImage.close()
        if (mFaceCallback == null)
            return
        //newThreadPool.execute {
            val faces = FaceUtil().getFaces(bytes, w, h)
            if (faces.isNotEmpty()) {
                for ((id, face) in faces.withIndex()) {
                    log(Thread.currentThread().name + ": face:[${id + 1}:${faces.size}] $face")
                }
                mFaceCallback?.onFace()
            } else {
                mFaceCallback?.onNoFace()
                log(Thread.currentThread().name + ": no any face")
            }
        //}
    }
}