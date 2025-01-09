#include <jni.h>
#include <string>
#include "Util.h"
#include "mouse_hook.h"


bool enableSensorHook = false;

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    doMouseHook();

    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_cn_martinkay_cursor2everything_MainActivity_call_1test_1function(JNIEnv *env, jobject thiz) {
    char text[0x100];
    sprintf(text, "(10 + 10) = %d", "1");
    return env->NewStringUTF(text);
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_martinkay_cursor2everything_dobby_Dobby_setStatus(JNIEnv *env, jobject thiz,
                                                          jboolean status) {
    enableSensorHook = status;
}