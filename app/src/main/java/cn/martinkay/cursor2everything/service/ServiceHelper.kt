package cn.martinkay.cursor2everything.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import cn.martinkay.cursor2everything.root.ShellUtils
import java.io.File

object ServiceHelper {

//    fun loadLibrary(locationManager: LocationManager, path: String): String? {
//        val rely = Bundle()
//        rely.putString("command_id", "load_library")
//        rely.putString("path", path)
//        if(locationManager.sendExtraCommand(PROVIDER_NAME, randomKey, rely)) {
//            return rely.getString("result")
//        }
//        return null
//    }

    fun initC2eLibrary(context: Context): Boolean {
        if (!ShellUtils.hasRoot()) return false

        val isX86: Boolean = runCatching {
            if (Build.SUPPORTED_ABIS.any { it.contains("x86") }) {
                return@runCatching true
            }
            val clazz = Class.forName("dalvik.system.VMRuntime")
            val method = clazz.getDeclaredMethod("getRuntime")
            val runtime = method.invoke(null)
            val field = clazz.getDeclaredField("vmInstructionSet")
            field.isAccessible = true
            val instructionSet = field.get(runtime) as String
            if (instructionSet.contains("x86")) {
                true
            } else false
        }.getOrElse { false }
        // todo: support x86

        val soDir = File("/data/local/c2e-lib")
        if (!soDir.exists()) {
            ShellUtils.executeCommand("mkdir ${soDir.absolutePath}")
        }
        val soFile = File(soDir, "libcursor2everything.so")
        runCatching {
            val tmpSoFile = File(soDir, "libcursor2everything.so.tmp").also { file ->
                var nativeDir = context.applicationInfo.nativeLibraryDir
                val apkSoFile = File(nativeDir, "libcursor2everything.so")
                if (apkSoFile.exists()) {
                    ShellUtils.executeCommand("cp ${apkSoFile.absolutePath} ${file.absolutePath}")
                } else {
                    Log.e(
                        "MockServiceHelper",
                        "Failed to copy c2e library: ${apkSoFile.absolutePath}"
                    )
                    return@runCatching
                }
            }
            if (soFile.exists()) {
                val originalHash =
                    ShellUtils.executeCommandToBytes("head -c 4096 ${soFile.absolutePath}")
                val newHash =
                    ShellUtils.executeCommandToBytes("head -c 4096 ${tmpSoFile.absolutePath}")
                if (originalHash.contentEquals(newHash)) {
                    ShellUtils.executeCommand("rm ${soFile.absolutePath}")
                    ShellUtils.executeCommand("mv ${tmpSoFile.absolutePath} ${soFile.absolutePath}")
                }
            } else if (tmpSoFile.exists()) {
                ShellUtils.executeCommand("mv ${tmpSoFile.absolutePath} ${soFile.absolutePath}")
            }
        }.onFailure {
            Log.w("MockServiceHelper", "Failed to copy c2e library", it)
        }

        ShellUtils.executeCommand("chmod 777 ${soFile.absolutePath}")
        return true
    }

    @SuppressLint("DiscouragedPrivateApi")
    fun loadC2eLibrary(path: String): Boolean {
        if (!ShellUtils.hasRoot()) return false

        if (path.isNotBlank()) {
            System.load(path)
        } else {
            val soDir = File("/data/local/c2e-lib")
            if (!soDir.exists()) {
                ShellUtils.executeCommand("mkdir ${soDir.absolutePath}")
            }
            val soFile = File(soDir, "libcursor2everything.so")

            runCatching {
                System.load(soFile.absolutePath)
            }.onSuccess {
                return true
            }.onFailure {
                return false
            }
        }
        return false
    }
}