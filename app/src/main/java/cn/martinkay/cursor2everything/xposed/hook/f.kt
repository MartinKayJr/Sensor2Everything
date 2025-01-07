package cn.martinkay.cursor2everything.xposed.hook

import android.app.Activity
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Toast
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.callbacks.XC_LoadPackage

object f : BaseHook() {
    // SettingActivityHook
    override val name: String = "f"

    override fun init(lpp: XC_LoadPackage.LoadPackageParam) {
        MethodFinder.fromClass("ai.nreal.nebula.activity.setting.SettingActivity")
            .filterByName("onCreate")
            .filterByParamTypes(Bundle::class.java)
            .first()
            .createHook {
                after {
                    val hmp = it
                    val activity = it.thisObject as Activity
                    it.thisObject.objectHelper().invokeMethodBestMatch(
                        "addTextView",
                        null,
                        "Cursor2Everything已注入",
                        12,
                        20,
                        OnClickListener {
                            Toast.makeText(activity, "QQ2250635418", Toast.LENGTH_SHORT).show()
                        })
                }
            }
    }

}