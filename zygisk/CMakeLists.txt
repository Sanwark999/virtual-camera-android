cmake_minimum_required(VERSION 3.22)

project(VirtualCameraHook)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set ABI
set(CMAKE_ANDROID_ARCH_ABI arm64-v8a)

# Add zygisk header only
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/zygisk/include)

# Main native library
add_library(vcamera SHARED
    src/main.cpp
    src/camera_hook.cpp
    src/camera_hook.h
)

target_link_libraries(vcamera
    log
)

# Set output directory
set_target_properties(vcamera PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}"
)
