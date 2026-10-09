#include <android/log.h>
#include <dlfcn.h>
#include <cstring>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "HookUtils", __VA_ARGS__))

extern "C" {

void* resolveSymbol(const char* library, const char* symbol) {
    void* handle = dlopen(library, RTLD_NOW);
    if (!handle) return nullptr;
    void* address = dlsym(handle, symbol);
    dlclose(handle);
    return address;
}

}
