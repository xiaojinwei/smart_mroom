package com.bnq.room.smartsimple

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bnq.room.lib_smart.Base64Util
import com.bnq.room.lib_smart.HWRecognitionHandle

class MainActivity : AppCompatActivity() {

    private var hwRecognitionHandle:HWRecognitionHandle? = null
    private var tvText:TextView? = null
    private var ivImage:ImageView? = null

    private val image_path = "test_img/2930_1682300144808472.5.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvText = findViewById<TextView>(R.id.text)
        ivImage = findViewById(R.id.image)
        hwRecognitionHandle = HWRecognitionHandle.init(this)
        setSimpleImage()
    }

    private fun setSimpleImage(){
        ivImage?.setImageBitmap(
            BitmapFactory.decodeStream(assets.open(image_path))
        )
    }

    fun test(){
        val input = assets.open(image_path)
        val byteArray = ByteArray(input.available())
        input.read(byteArray)
        val base64Str = Base64Util.base64EncodeToString(byteArray)
        println("----------base64Str: $base64Str")
        val result = hwRecognitionHandle?.performHandwritingRecognition(base64Str)
        tvText?.text = "识别结果: ${result?.res} \n置信度: ${result?.conf}"
    }

    fun test(view: View) {
        test()
    }
}