package com.shenlai.cameratool

import android.graphics.ImageFormat
import android.media.Image
import android.util.Log

object Image2NV21 {
    private const val TAG = "Image2NV21"
    //private const val VERBOSE = true
    private const val VERBOSE = false

    private fun isImageFormatSupported(image: Image): Boolean {
        when (image.format) {
            ImageFormat.YUV_420_888, ImageFormat.NV21, ImageFormat.YV12 -> return true
        }
        return false
    }

    fun getDataFromImage(image: Image): ByteArray {
        if (!isImageFormatSupported(image)) {
            throw RuntimeException("can't convert Image to byte array, format " + image.format)
        }
        val crop = image.cropRect
        val format = image.format
        val width = crop.width()
        val height = crop.height()
        val planes = image.planes
        val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
        val rowData = ByteArray(planes[0].rowStride)
        if (VERBOSE) Log.v(TAG, "get data from " + planes.size + " planes")
        var channelOffset = 0
        var outputStride = 1
        for (i in planes.indices) {
            when (i) {
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }
                1 -> {
                    channelOffset = width * height + 1
                    outputStride = 2
                }
                2 -> {
                    channelOffset = width * height
                    outputStride = 2
                }
            }
            val buffer = planes[i].buffer
            val rowStride = planes[i].rowStride
            val pixelStride = planes[i].pixelStride
            if (VERBOSE) {
                Log.v(TAG, "pixelStride $pixelStride")
                Log.v(TAG, "rowStride $rowStride")
                Log.v(TAG, "width $width")
                Log.v(TAG, "height $height")
                Log.v(TAG, "buffer size " + buffer.remaining())
            }
            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                val length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    buffer.get(data, channelOffset, length)
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    buffer.get(rowData, 0, length)
                    for (col in 0 until w) {
                        data[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
            if (VERBOSE) Log.v(TAG, "Finished reading data from plane $i")
        }
        return data
    }
}
