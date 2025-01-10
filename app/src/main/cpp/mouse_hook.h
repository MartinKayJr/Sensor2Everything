//
// Created by Administrator on 2025/1/8.
//

#ifndef CURSOR2EVERYTHING_MOUSE_HOOK_H
#define CURSOR2EVERYTHING_MOUSE_HOOK_H

// ssize_t SensorEventQueue::write(const sp<BitTube>& tube,
//        ASensorEvent const* events, size_t numEvents)
typedef int64_t (*OriginalSensorEventQueueWriteType)(void*, void*, int64_t);

// void convertToSensorEvent(const Event &src, sensors_event_t *dst);
typedef void (*OriginalConvertToSensorEventType)(void*, void*);

int doSensorHook();

int doUnSensorHook();

#endif //CURSOR2EVERYTHING_MOUSE_HOOK_H
