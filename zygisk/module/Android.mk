LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := virtualcamera_hook
LOCAL_SRC_FILES := src/main/cpp/entry.cpp src/main/cpp/camera_hook.cpp src/main/cpp/hook_utils.cpp
LOCAL_CFLAGS += -fPIC -O2
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)
