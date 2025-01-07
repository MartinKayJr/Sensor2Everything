package cn.martinkay.cursor2everything.xposed

import cn.martinkay.cursor2everything.xposed.hook.BaseHook
import cn.martinkay.cursor2everything.xposed.hook.InputManagerHook
import cn.martinkay.cursor2everything.xposed.hook.SensorEventCallbackHook
import cn.martinkay.cursor2everything.xposed.hook.ViewHook
import cn.martinkay.cursor2everything.xposed.hook.f
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

private const val PACKAGE_NAME_HOOKED = "com.xreal.evapro.nebula"
private const val TAG = "Xreal2Everything"

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == PACKAGE_NAME_HOOKED) {
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)

            initHooks(
                lpparam,
                SensorEventCallbackHook,
                f
            )
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
    }

    private fun initHooks(lpparam: LoadPackageParam, vararg hooks: BaseHook) {
        hooks.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.init(lpparam) // Pass lpparam to init
                it.isInit = true
                Log.i("Inited hook: ${it.name}")
            }.logexIfThrow("Failed init hook: ${it.name}")
        }
    }
}