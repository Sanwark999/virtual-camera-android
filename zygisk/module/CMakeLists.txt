cmake_minimum_required(VERSION 3.16)
project(virtualcamera_hook LANGUAGES CXX)

add_library(virtualcamera_hook SHARED
    src/main/cpp/entry.cpp
    src/main/cpp/camera_hook.cpp
    src/main/cpp/hook_utils.cpp
)

find_library(log-lib log)

target_link_libraries(virtualcamera_hook PRIVATE ${log-lib} dl)
