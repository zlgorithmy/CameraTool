package com.shenlai.cameratool

import android.graphics.Rect
import android.util.Log
import com.arcsoft.ageestimation.ASAE_FSDKAge
import com.arcsoft.ageestimation.ASAE_FSDKEngine
import com.arcsoft.ageestimation.ASAE_FSDKError
import com.arcsoft.ageestimation.ASAE_FSDKFace
import com.arcsoft.facedetection.AFD_FSDKEngine
import com.arcsoft.facedetection.AFD_FSDKError
import com.arcsoft.facerecognition.AFR_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKError
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKMatching
import com.arcsoft.facetracking.AFT_FSDKEngine
import com.arcsoft.facetracking.AFT_FSDKError
import com.arcsoft.facetracking.AFT_FSDKFace
import com.arcsoft.genderestimation.ASGE_FSDKEngine
import com.arcsoft.genderestimation.ASGE_FSDKError
import com.arcsoft.genderestimation.ASGE_FSDKFace
import com.arcsoft.genderestimation.ASGE_FSDKGender
import java.util.*

class FaceUtil {
    private var mDEBUG = false
    //人脸检测引擎
    private var mAFD_FSDKEngine: AFD_FSDKEngine? = null
    private var mAFD_FSDKError: AFD_FSDKError? = null
    //人脸跟踪引擎
    private var mAFT_FSDKEngine: AFT_FSDKEngine? = null
    private var mAFT_FSDKError: AFT_FSDKError? = null
    //人脸比对引擎
    private var mAFR_FSDKEngine: AFR_FSDKEngine? = null
    private var mAFR_FSDKError: AFR_FSDKError? = null
    //年龄检测引擎
    private var mASAE_FSDKEngine: ASAE_FSDKEngine? = null
    private var mASAE_FSDKError: ASAE_FSDKError? = null
    //性别检测引擎
    private var mASGE_FSDKEngine: ASGE_FSDKEngine? = null
    private var mASGE_FSDKError: ASGE_FSDKError? = null

    init {
        if(mDEBUG)Log.e("FaceUtil", "init")
        //initEngine()
    }

    private fun initAFD_Engine() {
        //初始化人脸检测引擎
        mAFD_FSDKEngine = AFD_FSDKEngine()
        mAFD_FSDKError = mAFD_FSDKEngine!!.AFD_FSDK_InitialFaceEngine(SDKKey.APP_ID, SDKKey.FD_KEY, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5)
        if(mDEBUG)Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + mAFD_FSDKError!!.code)
    }

    private fun initAFT_Engine() {
        //初始化人脸跟踪引擎
        mAFT_FSDKEngine = AFT_FSDKEngine()
        mAFT_FSDKError = mAFT_FSDKEngine!!.AFT_FSDK_InitialFaceEngine(SDKKey.APP_ID, SDKKey.FT_KEY, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5)
        if(mDEBUG)Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + mAFT_FSDKError!!.code)
    }

    private fun initAFR_Engine() {
        //初始化人脸识别引擎
        mAFR_FSDKEngine = AFR_FSDKEngine()
        mAFR_FSDKError = mAFR_FSDKEngine!!.AFR_FSDK_InitialEngine(SDKKey.APP_ID, SDKKey.FR_KEY)
        if(mDEBUG)Log.d(TAG, "AFR_FSDK_InitialEngine = " + mAFR_FSDKError!!.code)
    }

    private fun initASAE_Engine() {
        //初始化年龄检测引擎
        mASAE_FSDKEngine = ASAE_FSDKEngine()
        mASAE_FSDKError = mASAE_FSDKEngine!!.ASAE_FSDK_InitAgeEngine(SDKKey.APP_ID, SDKKey.AGE_KEY)
        if(mDEBUG)Log.d(TAG, "ASAE_FSDK_InitAgeEngine = " + mASAE_FSDKError!!.code)
    }

    private fun initASGE_Engine() {
        //初始化性别检测引擎
        mASGE_FSDKEngine = ASGE_FSDKEngine()
        mASGE_FSDKError = mASGE_FSDKEngine!!.ASGE_FSDK_InitgGenderEngine(SDKKey.APP_ID, SDKKey.GENDER_KEY)
        if(mDEBUG)Log.d(TAG, "ASGE_FSDK_InitgGenderEngine = " + mASGE_FSDKError!!.code)
    }

    private fun initEngine() {
        initAFD_Engine()
        initAFT_Engine()
        initAFR_Engine()
        initASAE_Engine()
        initASGE_Engine()
    }

    private fun uninitAFD_Engine() {
        //销毁人脸检测引擎
        mAFD_FSDKError = mAFD_FSDKEngine!!.AFD_FSDK_UninitialFaceEngine()
        if(mDEBUG)Log.d(TAG, "AFD_FSDK_UnInitialFaceEngine =" + mAFD_FSDKError!!.code)
    }

    private fun uninitAFT_Engine() {
        //销毁人脸跟踪引擎
        mAFT_FSDKError = mAFT_FSDKEngine!!.AFT_FSDK_UninitialFaceEngine()
        if(mDEBUG)Log.d(TAG, "AFT_FSDK_UnInitialFaceEngine =" + mAFT_FSDKError!!.code)
    }

    private fun uninitAFR_Engine() {
        //销毁人脸识别引擎
        mAFR_FSDKError = mAFR_FSDKEngine!!.AFR_FSDK_UninitialEngine()
        if(mDEBUG)Log.d(TAG, "AFR_FSDK_UnInitialEngine : " + mAFR_FSDKError!!.code)
    }

    private fun uninitASAE_Engine() {
        //销毁年龄检测引擎
        mASAE_FSDKError = mASAE_FSDKEngine!!.ASAE_FSDK_UninitAgeEngine()
        if(mDEBUG)Log.d(TAG, "ASAE_FSDK_UnInitAgeEngine =" + mASAE_FSDKError!!.code)
    }

    private fun uninitASGE_Engine() {
        //销毁性别检测引擎
        mASGE_FSDKError = mASGE_FSDKEngine!!.ASGE_FSDK_UninitGenderEngine()
        if(mDEBUG)Log.d(TAG, "ASGE_FSDK_UnInitGenderEngine =" + mASGE_FSDKError!!.code)
    }

    fun uninitEngine() {
        uninitAFD_Engine()
        uninitAFT_Engine()
        uninitAFR_Engine()
        uninitASAE_Engine()
        uninitASGE_Engine()
    }

    fun getFace(data: ByteArray, width: Int, height: Int): AFT_FSDKFace? {
        val faces = ArrayList<AFT_FSDKFace>()
        initAFT_Engine()

        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸跟踪返回结果保存在result。
        mAFT_FSDKError = mAFT_FSDKEngine!!.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, faces)

        uninitAFT_Engine()
        if (faces.size > 0) {
            val face = faces[0].clone()
            faces.clear()
            return face
        }
        return null
    }

    fun getFaces(data: ByteArray, width: Int, height: Int): List<AFT_FSDKFace> {
        val faces = ArrayList<AFT_FSDKFace>()

        initAFT_Engine()
        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸跟踪返回结果保存在result。
        mAFT_FSDKError = mAFT_FSDKEngine!!.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, faces)
        uninitAFT_Engine()
        return faces
    }

    //视频流人脸检测
    fun getFeature(data: ByteArray, width: Int, height: Int): AFR_FSDKFace? {
        val face = getFace(data, width, height) ?: return null
        val feature = AFR_FSDKFace()
        mAFR_FSDKError = mAFR_FSDKEngine!!.AFR_FSDK_ExtractFRFeature(data, width, height, AFR_FSDKEngine.CP_PAF_NV21, face.rect, face.degree, feature)
        if(mDEBUG)Log.d(TAG, "Face=" + feature.featureData[0] + "," + feature.featureData[1] + "," + feature.featureData[2] + "," + mAFR_FSDKError!!.code)
        return feature
    }

    fun getAge(data: ByteArray, width: Int, height: Int): Int {
        val face = getFace(data, width, height) ?: return 0

        // 用来存放检测到的人脸信息列表
        val result = ArrayList<ASAE_FSDKAge>()
        val input = ArrayList<ASAE_FSDKFace>()
        //这里人脸框和角度，请根据实际对应图片中的人脸框和角度填写
        input.add(ASAE_FSDKFace(face.rect, face.degree))

        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸检测返回结果保存在result。
        mASAE_FSDKError = mASAE_FSDKEngine!!.ASAE_FSDK_AgeEstimation_Image(data, width, height, ASAE_FSDKEngine.CP_PAF_NV21, input, result)
        if(mDEBUG)Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image =" + mASAE_FSDKError!!.code)
        if(mDEBUG)Log.d(TAG, "Face=" + result.size)
        for (age in result) {
            Log.d(TAG, "Age:" + age.age)
            return age.age
        }
        return 0
    }

    fun getGender(data: ByteArray, width: Int, height: Int): String {
        var genders = "UNKNOWN"
        val face = getFace(data, width, height)
        if (face == null) {
            Log.e(TAG, "getGender: face == null")
            return genders
        }

        // 用来存放检测到的人脸信息列表
        val result = ArrayList<ASGE_FSDKGender>()
        val input = ArrayList<ASGE_FSDKFace>()
        //这里人脸框和角度，请根据实际对应图片中的人脸框和角度填写
        input.add(ASGE_FSDKFace(Rect(210, 178, 478, 446), ASGE_FSDKEngine.ASGE_FOC_0))

        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸检测返回结果保存在result。
        mASGE_FSDKError = mASGE_FSDKEngine!!.ASGE_FSDK_GenderEstimation_Image(data, width, height, ASGE_FSDKEngine.CP_PAF_NV21, input, result)
        Log.d("com.arcsoft", "ASGE_FSDK_GenderEstimation_Image =" + mASGE_FSDKError!!.code)
        Log.d("com.arcsoft", "Face=" + result.size)
        for (gender in result) {
            when (gender.gender) {
                ASGE_FSDKGender.FEMALE -> {
                    Log.d(TAG, "gender: FEMALE")
                    genders = "FEMALE"
                }
                ASGE_FSDKGender.MALE -> {
                    Log.d(TAG, "gender: MALE")
                    genders = "MALE"
                }
                ASGE_FSDKGender.UNKNOWN -> Log.d(TAG, "gender: UNKNOWN")
                else -> {
                }
            }
        }
        return genders
    }

    fun featureMatch(face1: AFR_FSDKFace, face2: AFR_FSDKFace): Float {
        //score 用于存放人脸对比的相似度值
        val score = AFR_FSDKMatching()
        mAFR_FSDKError = mAFR_FSDKEngine!!.AFR_FSDK_FacePairMatching(face1, face2, score)
        Log.d(TAG, "AFR_FSDK_FacePairMatching=" + mAFR_FSDKError!!.code)
        Log.d(TAG, "Score:" + score.score)
        return score.score
    }

    companion object {
        private val TAG = FaceUtil::class.java.simpleName
        private var mInstance: FaceUtil? = null

        fun getInstance(): FaceUtil? {
            if (mInstance == null) {
                synchronized(FaceUtil::class.java) {
                    if (mInstance == null) {
                        mInstance = FaceUtil()
                    }
                }
            }
            return mInstance
        }
    }
}