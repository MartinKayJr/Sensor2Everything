package cn.martinkay.cursor2everything.xposed.hook

import cn.martinkay.cursor2everything.root.ShellUtils
import cn.martinkay.cursor2everything.service.ServiceHelper
import cn.martinkay.cursor2everything.xposed.MainHook
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.PrintWriter

object InputShellCommandHook : BaseHook() {

    override val name: String = "InputShellCommandHook"

    var mIMS: Any? = null
    var mPMS: Any? = null

    override fun init(lpp: XC_LoadPackage.LoadPackageParam) {

        MethodFinder.fromClass("com.android.server.input.InputShellCommand")
            .filterByName("onCommand")
            .onEach { it ->
                it.createHook {
                    before {
                        val cmd = it.args[0] as String
                        if ("c2e" != cmd) {
                            return@before
                        }

                        val thisObject = it.thisObject
                        var path = ""
                        if ("-p" == cmd) {
                            path = thisObject.objectHelper()
                                .invokeMethodBestMatch("getNextArgRequired", String::class.java) as String
                        }
                        val type = thisObject.objectHelper()
                            .invokeMethodBestMatch("getNextArgRequired", String::class.java) as String
                        val printWriter = thisObject.objectHelper()
                            .invokeMethodBestMatch("getOutPrintWriter") as PrintWriter
                        if ("init" == type) {
                            printWriter.println("Cursor2Everything for u server")
                            printWriter.println("检测root: " + ShellUtils.hasRoot())
                            if (ShellUtils.hasRoot()) {
                                ShellUtils.setEnforceMode(false) // 关闭SELinux
                                if(ServiceHelper.loadC2eLibrary(path)) {
                                    printWriter.println("sensor劫持成功")
                                } else {
                                    printWriter.println("sensor无法劫持传感器")
                                }
                            }
                        }
                        it.result = 0
                    }
                }
            }


        MethodFinder.fromClass("com.android.server.pm.PackageManagerShellCommand")
            .filterByName("onCommand")
            .onEach { it ->
                it.createHook {
                    before {
                        val cmd = it.args[0] as String
                        if ("c2e" != cmd) {
                            return@before
                        }
                        val thisObject = it.thisObject
                        val type = thisObject.objectHelper()
                            .invokeMethodBestMatch("getNextArgRequired", String::class.java) as String
                        val printWriter = thisObject.objectHelper()
                            .invokeMethodBestMatch("getOutPrintWriter") as PrintWriter
                        if ("c" == type) {
                            printWriter.println("Cursor2Everything for u server")
                        }
                        it.result = 0
                    }
                }
            }


        val imsClass = XposedHelpers.findClassIfExists(
            "com.android.server.input.InputManagerService",
            EzXHelper.classLoader
        )

        XposedBridge.hookAllConstructors(imsClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                mIMS = param.thisObject
            }
        })

        MainHook.deoptimizeMethod(imsClass, "onShellCommand")

        val pmsClass = XposedHelpers.findClassIfExists(
            "com.android.server.pm.PackageManagerService",
            EzXHelper.classLoader
        )
        XposedBridge.hookAllConstructors(pmsClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                mPMS = param.thisObject
            }
        })
        MainHook.deoptimizeMethod(pmsClass, "onShellCommand")




    }
}