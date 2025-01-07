package cn.martinkay.cursor2everything.xposed.hook

import android.util.Log
import android.view.MotionEvent
import com.alibaba.fastjson.JSON
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.callbacks.XC_LoadPackage

object ViewHook : BaseHook() {
    override fun init(lpp: XC_LoadPackage.LoadPackageParam) {
        MethodFinder.fromClass("android.view.View")
            .filterByName("onGenericMotionEvent")
            .filterByParamTypes(MotionEvent::class.java)
            .first()
            .createHook {
                before {
                    val motionEvent = it.args[0] as MotionEvent
                    Log.i("Cursor2Everything", JSON.toJSONString(motionEvent))
                }
            }
    }

    override val name: String
        get() = "ViewHook"
}