#include <jni.h>
#include <android/log.h>
#include <dlfcn.h>
#include <cstring>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "CameraHook", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "CameraHook", __VA_ARGS__))

// This is a template only.
// Real production code must hook the actual platform Camera2 / CameraManager implementation
// for the rooted Pixel 4 XL firmware and validate behavior with each app.
extern "C" {
    jint Java_android_hardware_camera2_CameraManager_openCamera(
        JNIEnv* env,
        jobject /* thiz */,
        jstring /* cameraId */,
        jobject /* callback */,
        jobject /* handler */
    ) {
        LOGI("Camera2 openCamera intercepted");
        return 0;
    }

    jboolean Java_android_hardware_camera2_CameraCharacteristics_hasCamera(
        JNIEnv* env,
        jobject /* thiz */,
        jstring /* keyName */
    ) {
        LOGI("CameraCharacteristics query intercepted");
        return true;
    }
}
