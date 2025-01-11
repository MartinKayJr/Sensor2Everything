package cn.martinkay.cursor2everything.xposed

import cn.martinkay.cursor2everything.xposed.hook.BaseHook
import cn.martinkay.cursor2everything.xposed.hook.InputShellCommandHook
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method

private const val TAG = "Cursor2Everything"

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    companion object {
        var deoptimizeMethod: Method? = null

        init {
            var m: Method? = null
            try {
                m = XposedBridge::class.java.getDeclaredMethod(
                    "deoptimizeMethod",
                    Member::class.java
                )
            } catch (t: Throwable) {
                XposedBridge.log(
                    android.util.Log.getStackTraceString(
                        t
                    )
                )
            }
            deoptimizeMethod = m
        }

        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        fun deoptimizeMethod(c: Class<*>, n: String) {
            for (m in c.declaredMethods) {
                if (deoptimizeMethod != null && m.name == n) {
                    deoptimizeMethod!!.invoke(null, m)
//                    if (BuildConfig.DEBUG) XposedBridge.log(" Deoptimized " + m.name)
                    XposedBridge.log(" Deoptimized " + m.name)
                }
            }
        }
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (("android" == lpparam.packageName) && (lpparam.processName == "android")) {
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)

            initHooks(
                lpparam,
                InputShellCommandHook
//                SensorEventCallbackHook,
//                f
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