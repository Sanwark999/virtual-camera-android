#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <dlfcn.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "VirtualCameraHook", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "VirtualCameraHook", __VA_ARGS__))

extern "C" {

__attribute__((visibility("default")))
void zygisk_module_entry() {
    LOGI("Zygisk module entry loaded.");
}

__attribute__((visibility("default")))
void nativeLoad() {
    LOGI("Native load called.");
}

}
