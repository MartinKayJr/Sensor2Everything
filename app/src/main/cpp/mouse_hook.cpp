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
static int printCount = 0;  // 静态计数器，用于记录打印次数

void ConvertToSensorEvent(void *src, void *dst) {
    if (enableSensorHook) {
        // 传感器标识符
        auto sensorHandle = *(int32_t *)((char*)src + 4); // src.sensorHandle
        // 传感器类型
        auto sensorType = *(int32_t *)((char*)src + 8); // src.sensorType
        // 以纳秒为单位的时间，在“elapsedRealtimeNano（）”的时基中。
        auto timestamp = *(int64_t *)((char*)src + 16); // src.timestamp


        *(int64_t *)((char*)dst + 16) = 0LL; // dst.timestamp = 0
        *(int32_t *)((char*)dst + 24) = 0; // dst.reserved0 = 0
        *(int64_t *)((char*)dst) = timestamp; // dst.version = timestamp
        *(int32_t *)((char*)dst + 8) = sensorHandle;  // dst.sensor = sensorHandle
        *(int32_t *)((char*)dst + 12) = sensorType; // dst.type = sensorType
        *(int8_t *)((char*)dst + 28) = sensorType; // dst.reserved1 = sensorType

        // 一次步数数据
        if (sensorType == 18) {
            *(float *)((char*)dst + 16) = -1.0; // dst.timestamp = -1.0 (float)
        } else if (sensorType == 19) {
            // 计步历史累加值
            *(int64_t *)((char*)dst + 16) = -1; // dst.timestamp = -1 (int64_t)
        } else {
            *(float *)((char*)dst + 16) = -1.0; // dst.timestamp = -1.0 (float)
            *(float *)((char*)dst + 24) = -1.0; // dst.reserved0 = -1.0 (float)
            *(int8_t *)((char*)dst + 28) = *(int8_t *)((char*)src + 36); // dst.reserved1 = src.reserved1
        }

        if (printCount < 10000) {
            LOGE("current sensor type : %d", sensorType);
            // 检查是否为加速度传感器
            if (sensorType == 1) {  // SENSOR_TYPE_ACCELEROMETER
                float accelX = *(float *)((char*)dst + 32);  // data[0]: x-axis
                float accelY = *(float *)((char*)dst + 36);  // data[1]: y-axis
                float accelZ = *(float *)((char*)dst + 40);  // data[2]: z-axis
                // 打印加速度数据
                LOGE("Accelerometer Data [%d]: x=%f, y=%f, z=%f", printCount + 1, accelX, accelY, accelZ);
            } else if (sensorType == 40) {
                float gyroX = *(float *) ((char *) dst + 32);  // data[0]: x-axis
                float gyroY = *(float *) ((char *) dst + 36);  // data[1]: y-axis
                float gyroZ = *(float *) ((char *) dst + 40);  // data[2]: z-axis

                // 打印陀螺仪数据
                LOGE("Gyroscope Data [%d]: x=%f, y=%f, z=%f", printCount + 1, gyroX, gyroY, gyroZ);
            }
            printCount++;  // 增加计数器
        }
    } else {
        OriginalConvertToSensorEvent(src, dst);
    }

    if (enableSensorHook) {
        LOGE("ConvertToSensorEvent called");
    }
}

void *sensorWrite = nullptr;
void *convertToSensorEvent = nullptr;

int doSensorHook() {
    SandHook::ElfImg sensorService(LIBSF_PATH);

    if (!sensorService.isValid()) {
        LOGE("failed to load libsensorservice");
        return -1;
    }

    sensorWrite = sensorService.getSymbolAddress<void*>("_ZN7android16SensorEventQueue5writeERKNS_2spINS_7BitTubeEEEPK12ASensorEventm");
    if (sensorWrite == nullptr) {
        sensorWrite = sensorService.getSymbolAddress<void*>("_ZN7android16SensorEventQueue5writeERKNS_2spINS_7BitTubeEEEPK12ASensorEventj");
    }

    convertToSensorEvent = sensorService.getSymbolAddress<void*>("_ZN7android8hardware7sensors4V1_014implementation20convertToSensorEventERKNS2_5EventEP15sensors_event_t");

    LOGE("Dobby SensorEventQueue::write found at %p", sensorWrite);
    LOGE("Dobby convertToSensorEvent found at %p", convertToSensorEvent);

    if (sensorWrite != nullptr) {
        OriginalSensorEventQueueWrite = (OriginalSensorEventQueueWriteType)InlineHook(sensorWrite, (void *)SensorEventQueueWrite);
    }

    if (convertToSensorEvent != nullptr) {
        OriginalConvertToSensorEvent = (OriginalConvertToSensorEventType)InlineHook(convertToSensorEvent, (void *)ConvertToSensorEvent);
    }
    return 0;
}

int doUnSensorHook() {
    int result = -1;
    if (OriginalSensorEventQueueWrite != nullptr) {
        result = DobbyDestroy(sensorWrite);
    }
    if (OriginalConvertToSensorEvent != nullptr) {
        result = DobbyDestroy(convertToSensorEvent);
    }
    return result;
}