package com.bnq.room.lib_smart

import android.util.Base64
import java.nio.charset.Charset

class Base64Util {

    companion object{

        /**
         * 字符编码 UTF-8
         */
        const val CHARSET_UTF8 = "UTF-8";

        fun base64Encode(array:ByteArray):ByteArray = Base64.encode(array, Base64.NO_WRAP)
        fun base64EncodeToString(array:ByteArray):String = base64Encode(array).toString(Charset.forName(CHARSET_UTF8))
        fun base64Encode(str:String):ByteArray = base64Encode(str.toByteArray())
        fun base64EncodeToString(str:String):String = base64Encode(str).toString(Charset.forName(CHARSET_UTF8))
        fun base64Decode(str:String):ByteArray = Base64.decode(str, Base64.NO_WRAP)
        fun base64DecodeToString(str:String):String = base64Decode(str).toString(Charset.forName(CHARSET_UTF8))
    }

}