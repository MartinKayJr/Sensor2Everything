package cn.martinkay.cursor2everything.xposed.hook

import android.util.Log
import android.view.InputEvent
import com.alibaba.fastjson.JSON
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.callbacks.XC_LoadPackage

object InputManagerHook : BaseHook() {
    override fun init(lpp: XC_LoadPackage.LoadPackageParam) {
        MethodFinder.fromClass("android.hardware.input.InputManagerGlobal")
            .filterByName("injectInputEvent")
            .filterByParamTypes(InputEvent::class.java, Int::class.java)
            .first()
            .createHook {
                before {
                    val inputEvent = it.args[0] as InputEvent
                    val mode = it.args[1] as Int
                    Log.i("Cursor2Everything", JSON.toJSONString(inputEvent))
                }
            }
    }

    override val name: String
        get() = "InputManagerHook"
}