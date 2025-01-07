package cn.martinkay.cursor2everything.xposed.hook

import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.util.Log
import com.alibaba.fastjson.JSON
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.callbacks.XC_LoadPackage

object SensorEventCallbackHook : BaseHook() {
    override val name: String = "SensorEventCallbackHook"


    override fun init(lpp: XC_LoadPackage.LoadPackageParam) {
        MethodFinder.fromClass("ai.nreal.sensorcalib.BiasUtils$1")
            .filterByName("onSensorChanged")
            .filterByParamTypes(SensorEvent::class.java)
            .first()
            .createHook {
                before {
                    val sensorEvent = it.args[0] as SensorEvent
                    Log.i("SensorEventCallbackHook->", JSON.toJSONString(sensorEvent))
                }
            }
    }
}