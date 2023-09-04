package com.bnq.room.lib_smart

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import java.util.*

public data class Result(
    var res: String? = null,
    var conf: Float? = null
) {}

internal class HWRecPerformer(
) {
    var character: Array<String> = arrayOf(
        "</s>", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c",
        "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
        "s", "t", "u" , "v", "w", "x", "y", "z")
    private fun argmax(array: FloatArray): Int {
        var maxIndex = 0
        var maxValue = Float.NEGATIVE_INFINITY
        for (i in array.indices) {
            if (array[i] > maxValue) {
                maxValue = array[i]
                maxIndex = i
            }
        }
        return maxIndex
    }
    private fun postProcess(modelResult: Array<FloatArray>): Pair<String, Float>  {
        val labelVals = modelResult.copyOf()

        var res: String = ""
        var sum: Float = .0f

        for (i in labelVals.indices) {
            var labeIndex = argmax(labelVals[i])
            var labeConf = labelVals[i].max()?:0.0f
            if (labeIndex == 0){
                break
            }
            res += character[labeIndex]
            sum += labeConf
        }
        var conf = sum / res.length

        return Pair(res, conf)
    }

    fun upscale(inputStream: InputStream, ortEnv: OrtEnvironment, ortSession: OrtSession): Result {
        var result = Result()
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Step 1: convert image into byte array (raw image bytes)
        val resizeBitmap = bitmap.let { Bitmap.createScaledBitmap(it, 128, 32, false) }
        val imgData = preProcess(resizeBitmap)

        val shape = longArrayOf(1, 3, 32, 128)

        val inputName = ortSession?.inputNames?.iterator()?.next()

        val inputTensor = OnnxTensor.createTensor(
            ortEnv,
            imgData,
            shape,
        )
        inputTensor.use {
            // Step 3: call ort inferenceSession run
            val output = ortSession.run(Collections.singletonMap(inputName, inputTensor))
            output.use {
                val rawOutput = ((output?.get(0)?.value) as Array<Array<FloatArray>>)[0]
                val (res, conf) = postProcess(rawOutput)
                result.res = res
                result.conf = conf
            }
        }
        return result
    }

}