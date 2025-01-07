package cn.martinkay.cursor2everything

import android.hardware.SensorEventCallback
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.bytedance.android.bytehook.ByteHook

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("cursor2everything");
        }
    }

    private var showText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        showText = findViewById(R.id.textBloardId)
        ByteHook.init();
        val sensor = this.getSystemService("sensor") as SensorManager
        sensor.getDefaultSensor(35)
    }


    fun call_native_function(view: View) {
        val result = call_test_function()
        showText?.text = result
    }

    fun dobby_load(view: View) {

    }

    external fun call_test_function(): String?
}