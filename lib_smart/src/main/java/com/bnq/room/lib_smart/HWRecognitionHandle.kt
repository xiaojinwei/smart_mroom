package com.bnq.room.lib_smart

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import android.content.Context
import java.io.ByteArrayInputStream
import java.io.InputStream

class HWRecognitionHandle private constructor(private val ortEnv:OrtEnvironment,
        private val ortSession: OrtSession){

    companion object{
        @JvmStatic
        fun init(context: Context):HWRecognitionHandle{
            var ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
            val sessionOptions: OrtSession.SessionOptions = OrtSession.SessionOptions()
            sessionOptions.registerCustomOpLibrary(OrtxPackage.getLibraryPath())
            var ortSession: OrtSession = ortEnv.createSession(readModel(context), sessionOptions)
            return HWRecognitionHandle(ortEnv, ortSession)
        }

        private fun readModel(context: Context):ByteArray{
            val modelID = R.raw.inference_290_quant
            return context.resources.openRawResource(modelID).readBytes()
        }

    }

    private var superResPerformer = HWRecPerformer()

    fun performHandwritingRecognition(base64StrImage:String):Result{
        val byteArray = Base64Util.base64Decode(base64StrImage)
        return performHandwritingRecognition(byteArray)
    }

    fun performHandwritingRecognition(byteArray: ByteArray):Result{
        val input = ByteArrayInputStream(byteArray)
        return performHandwritingRecognition(input)
    }

    fun performHandwritingRecognition(input:InputStream):Result{
        return superResPerformer.upscale(input, ortEnv, ortSession)
    }

    fun close(){
        ortEnv.close()
        ortSession.close()
    }

}