package cn.martinkay.cursor2everything

import android.hardware.SensorEventCallback
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import cn.martinkay.cursor2everything.service.ServiceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        val initC2eLibrary = ServiceHelper.initC2eLibrary(this@MainActivity)
        showToast("初始化so状态:$initC2eLibrary")
    }


    fun call_native_function(view: View) {
        val result = call_test_function()
        showText?.text = result
    }

    fun dobby_load(view: View) {

    }

    external fun call_test_function(): String?

    private fun showToast(message: String) = lifecycleScope.launch(Dispatchers.Main) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}