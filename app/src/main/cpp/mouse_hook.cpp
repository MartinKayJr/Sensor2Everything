//
// Created by Administrator on 2025/1/8.
//
#include <dobby.h>
#include <unistd.h>
#include "mouse_hook.h"
#include "Util.h"
#include "elf_util.h"
#include "dobby_hook.h"

#define LIBSF_PATH "/system/lib64/libsensorservice.so"

extern bool enableSensorHook;

// _ZN7android16SensorEventQueue5writeERKNS_2spINS_7BitTubeEEEPK12ASensorEventm
OriginalSensorEventQueueWriteType OriginalSensorEventQueueWrite = nullptr;

OriginalConvertToSensorEventType OriginalConvertToSensorEvent = nullptr;


int64_t SensorEventQueueWrite(void *tube, void *events, int64_t numEvents) {
    if (enableSensorHook) {
        LOGD("SensorEventQueueWrite called");
    }
    return OriginalSensorEventQueueWrite(tube, events, numEvents);
}

// ConvertToSensorEvent https://cs.android.com/android/platform/superproject/+/android14-qpr3-release:hardware/interfaces/sensors/aidl/convert/convert.cpp;l=86?q=convertToSensorEvent&ss=android%2Fplatform%2Fsuperproject
// https://developer.android.google.cn/develop/sensors-and-location/sensors/sensors_motion?hl=zh-cn
// Event src https://cs.android.com/android/platform/superproject/+/android14-qpr3-release:hardware/interfaces/sensors/aidl/android/hardware/sensors/Event.aidl
// SensorType https://cs.android.com/android/platform/superproject/+/android14-qpr3-release:hardware/interfaces/sensors/aidl/android/hardware/sensors/SensorType.aidl
// sensor.h dst https://android.googlesource.com/platform/hardware/libhardware/+/refs/heads/main/include_all/hardware/sensors.h
void ConvertToSensorEvent(void *src, void *dst) {
    if (enableSensorHook) {
        // 传感器标识符
        auto sensorHandle = *(int32_t *)((char*)src + 4);
        // 传感器类型
        auto sensorType = *(int32_t *)((char*)src + 8);
        // 以纳秒为单位的时间，在“elapsedRealtimeNano（）”的时基中。
        auto timestamp = *(int64_t *)((char*)src + 16);


        *(int64_t *)((char*)dst + 16) = 0LL;
        *(int32_t *)((char*)dst + 24) = 0;
        *(int64_t *)((char*)dst) = timestamp;
        *(int32_t *)((char*)dst + 8) = sensorHandle;
        *(int32_t *)((char*)dst + 12) = sensorType;
        *(int8_t *)((char*)dst + 28) = sensorType;

        // 一次步数数据
        if (sensorType == 18) {
            *(float *)((char*)dst + 16) = -1.0;
        } else if (sensorType == 19) {
            // 计步历史累加值
            *(int64_t *)((char*)dst + 16) = -1;
        } else {
            *(float *)((char*)dst + 16) = -1.0;
            *(float *)((char*)dst + 24) = -1.0;
            *(int8_t *)((char*)dst + 28) = *(int8_t *)((char*)src + 36);
        }
    } else {
        OriginalConvertToSensorEvent(src, dst);
    }

    if (enableSensorHook) {
        LOGD("ConvertToSensorEvent called");
    }
}



void doMouseHook() {
    SandHook::ElfImg sensorService(LIBSF_PATH);

    if (!sensorService.isValid()) {
        LOGE("failed to load libsensorservice");
        return;
    }

    auto sensorWrite = sensorService.getSymbolAddress<void*>("_ZN7android16SensorEventQueue5writeERKNS_2spINS_7BitTubeEEEPK12ASensorEventm");
    if (sensorWrite == nullptr) {
        sensorWrite = sensorService.getSymbolAddress<void*>("_ZN7android16SensorEventQueue5writeERKNS_2spINS_7BitTubeEEEPK12ASensorEventj");
    }

    auto convertToSensorEvent = sensorService.getSymbolAddress<void*>("_ZN7android8hardware7sensors4V1_014implementation20convertToSensorEventERKNS2_5EventEP15sensors_event_t");

    LOGD("Dobby SensorEventQueue::write found at %p", sensorWrite);
    LOGD("Dobby convertToSensorEvent found at %p", convertToSensorEvent);

    if (sensorWrite != nullptr) {
        OriginalSensorEventQueueWrite = (OriginalSensorEventQueueWriteType)InlineHook(sensorWrite, (void *)SensorEventQueueWrite);
    }

    if (convertToSensorEvent != nullptr) {
        OriginalConvertToSensorEvent = (OriginalConvertToSensorEventType)InlineHook(convertToSensorEvent, (void *)ConvertToSensorEvent);
    }
}