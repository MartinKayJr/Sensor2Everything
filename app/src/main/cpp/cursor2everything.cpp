#include <jni.h>
#include <string>
#include "bytehook.h"
#include "Util.h"

static void (*orig_move)(float deltaX, float deltaY);

static void my_libinputservice_move(float deltaX, float deltaY) {
    // 执行 stack 清理（不可省略）
    BYTEHOOK_STACK_SCOPE();
    LOGE("my_libinputservice_move: deltaX=%f, deltaY=%f", deltaX, deltaY);
    BYTEHOOK_CALL_PREV(my_libinputservice_move, deltaX, deltaY);
}

__attribute__((constructor)) static void dylibInject() {
    bytehook_get_records(BYTEHOOK_RECORD_ITEM_ALL);
    LOGE("my_libinputservice_move: start");
    bytehook_hook_all(
            NULL
            , "_ZN7android21MouseCursorController4moveEff"
            , reinterpret_cast<void *>(my_libinputservice_move)
            , NULL
            , NULL);
    LOGE("my_libinputservice_move: over");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_martinkay_cursor2everything_MainActivity_call_1test_1function(JNIEnv *env, jobject thiz) {
    char text[0x100];
    sprintf(text, "(10 + 10) = %d", "1");
    return env->NewStringUTF(text);
}