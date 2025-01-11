# Sensor2Everything

# 使用方法
通过adb命令输入input c2e init进行hook初始化

input c2e unhook 解除hook

input c2e hook 再次hook



## 陀螺仪传感器
```json
{
  "accuracy": 3,
  "firstEventAfterDiscontinuity": false,
  "sensor": {
    "additionalInfoSupported": true,
    "dataInjectionSupported": false,
    "dynamicSensor": false,
    "fifoMaxEventCount": 10000,
    "fifoReservedEventCount": 0,
    "handle": 16777377,
    "highestDirectReportRateLevel": 3,
    "id": 0,
    "maxDelay": 1000000,
    "maximumRange": 17.453018,
    "minDelay": 2404,
    "name": "lsm6dso Gyroscope-Uncalibrated Non-wakeup",
    "power": 0.55,
    "reportingMode": 0,
    "requiredPermission": "",
    "resolution": 0.0006108648,
    "stringType": "android.sensor.gyroscope_uncalibrated",
    "type": 16,
    "uuid": "00000000-0000-0000-0000-000000000000",
    "vendor": "STMicro",
    "version": 142926,
    "wakeUpSensor": false
  },
  "timestamp": 609558951653965,
  "values": [
    -0.0006108648,
    -0.0022143847,
    -0.0001527162,
    -0.0022698126,
    -0.0018143391,
    -0.00038025097
  ]
}
```

## 加速度传感器
```json
{
    "accuracy": 3,
    "firstEventAfterDiscontinuity": false,
    "sensor": {
        "additionalInfoSupported": false,
        "dataInjectionSupported": false,
        "dynamicSensor": false,
        "fifoMaxEventCount": 10000,
        "fifoReservedEventCount": 3000,
        "handle": 16777567,
        "highestDirectReportRateLevel": 3,
        "id": 0,
        "maxDelay": 1000000,
        "maximumRange": 156.9064,
        "minDelay": 2404,
        "name": "lsm6dso Accelerometer-Uncalibrated Non-wakeup",
        "power": 0.17,
        "reportingMode": 0,
        "requiredPermission": "",
        "resolution": 0.0047856453,
        "stringType": "android.sensor.accelerometer_uncalibrated",
        "type": 35,
        "uuid": "00000000-0000-0000-0000-000000000000",
        "vendor": "STMicro",
        "version": 142926,
        "wakeUpSensor": false
    },
    "timestamp": 609558949829225,
    "values": [
        -0.21355942,
        0.1591227,
        9.875777,
        0,
        0,
        0
    ]
}
```

## 地磁传感器
```json
{
    "accuracy": 3,
    "firstEventAfterDiscontinuity": false,
    "sensor": {
        "additionalInfoSupported": false,
        "dataInjectionSupported": false,
        "dynamicSensor": false,
        "fifoMaxEventCount": 10000,
        "fifoReservedEventCount": 600,
        "handle": 16777357,
        "highestDirectReportRateLevel": 1,
        "id": 0,
        "maxDelay": 1000000,
        "maximumRange": 4912.0503,
        "minDelay": 10000,
        "name": "ak0991x Magnetometer-Uncalibrated Non-wakeup",
        "power": 1.1,
        "reportingMode": 0,
        "requiredPermission": "",
        "resolution": 0.15,
        "stringType": "android.sensor.magnetic_field_uncalibrated",
        "type": 14,
        "uuid": "00000000-0000-0000-0000-000000000000",
        "vendor": "akm",
        "version": 146990,
        "wakeUpSensor": false
    },
    "timestamp": 609558947718235,
    "values": [
        144.73126,
        87.65625,
        331.1625,
        179.04,
        70.08,
        362.46
    ]
}
```
