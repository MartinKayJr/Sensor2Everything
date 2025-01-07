package cn.martinkay.cursor2everything.xposed.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

abstract class BaseHook() {
    abstract fun init(lpp: LoadPackageParam)
    abstract val name: String
    var isInit: Boolean = false
}