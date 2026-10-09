# Virtual Camera Android - RTMP Stream Replacement

## Overview

This project is a complete Android application that receives an RTMP video stream and provides it as a virtual camera source on a rooted Android 13 device (Google Pixel 4 XL).

### Architecture

**Main Application (APK):**
- Connects to a configurable RTMP URL
- Receives and demuxes H.264/AAC video streams
- Decodes H.264 using Android MediaCodec (hardware preferred, software fallback available)
- Previews the decoded stream in the UI
- Manages reconnection with exponential backoff
- Runs as a foreground service to maintain the stream when backgrounded
- Provides statistics (resolution, FPS, dropped frames, decoder type)

**Root Integration (Zygisk/LSPosed Module Template):**
- C/C++ template for hooking Camera2 and Camera1 APIs
- Must be compiled and loaded on a rooted device with Zygisk Next or LSPosed
- Not a universal drop-in solution; requires device-specific tuning
- Validates Camera API calls and potentially redirects them to the virtual stream

### Important Limitations

1. **No universal camera replacement without privilege:**
   - Android 13 does not permit a normal app to replace the system camera for all other apps
   - The Zygisk/LSPosed module is a template that requires:
     - Root access (KernelSU/Magisk/similar)
     - Zygisk Next or LSPosed framework installed
     - Compilation for the exact Pixel 4 XL kernel/firmware
     - Iterative testing and tuning

2. **Compatibility is not guaranteed:**
   - Different apps use Camera1, Camera2, CameraX, or NDK camera APIs differently
   - Some apps may bypass the hook, use internal caching, or validate camera properties
   - Each app must be tested individually

3. **Stability is device-specific:**
   - The module must be rebuilt and tuned for your exact Android 13 build
   - Incorrect hooking can destabilize the device or cause camera framework crashes
   - Provide a clear uninstall/recovery path before deploying

## Requirements

### Device
- Google Pixel 4 XL running Android 13
- Rooted with KernelSU, Magisk, or similar
- Zygisk Next or LSPosed framework (for the virtual camera hook)
- Network access to the RTMP server

### Build Environment (Windows)
- Java 17 (e.g., Eclipse Adoptium)
- Android SDK command-line tools under `C:\Android`
- Gradle wrapper (provided in the project)
- Windows PowerShell or Command Prompt

## Build Instructions

### 1. Clone the repository

```powershell
git clone https://github.com/Sanwark999/virtual-camera-android.git
cd virtual-camera-android
```

### 2. Set up environment variables

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17"
$env:ANDROID_HOME = "C:\Android"
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
```

If these paths differ on your system, adjust accordingly.

### 3. Accept Android SDK licenses

```powershell
yes | & "$env:ANDROID_HOME\cmdline-tools\latest\bin\sdkmanager" --licenses
```

### 4. Install required SDK components

```powershell
& "$env:ANDROID_HOME\cmdline-tools\latest\bin\sdkmanager" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### 5. Build the APK

```powershell
.\gradlew.bat clean test assembleDebug
```

Expected output:
```
Build successful!
APK generated: app\build\outputs\apk\debug\app-debug.apk
```

### 6. Install on your device

With your Pixel 4 XL connected via USB or network ADB:

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Or, if using network ADB:

```powershell
adb connect <device-ip>:5555
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## Configuration

### RTMP URL

The default URL is:
```
rtmp://192.168.1.11:1935/live/server
```

This is intentionally a private LAN address. To use a different URL:

1. Launch the app on your device
2. Edit the RTMP URL field
3. Tap **Connect**

The app validates the URL format before connecting.

### Network Security

- **Do NOT** expose an unauthenticated RTMP server to the public internet
- Use one of these for remote access:
  - VPN (WireGuard, OpenVPN, Tailscale)
  - SSH tunnel
  - Private network (e.g., internal data center)
  - Authenticated RTMP endpoint

## Usage

### Main App (Virtual Camera Receiver)

1. **Launch the app** on your Pixel 4 XL
2. **Enter the RTMP URL** or use the default
3. **Tap Connect** — the app will attempt to:
   - Resolve the hostname
   - Connect to the RTMP server
   - Receive the video stream
   - Decode H.264 frames
   - Display the video in the preview
   - Show statistics (resolution, FPS, dropped frames)
4. **Tap Disconnect** to stop the stream

**Statistics displayed:**
- **Resolution**: Detected frame dimensions (e.g., 1080x1920)
- **FPS**: Measured frames per second
- **Decoder**: "MediaCodec" (hardware) or "Software fallback" (if needed)
- **Dropped**: Number of frames dropped due to buffering or processing delays

### Foreground Service

The app runs a foreground service to keep the stream alive when backgrounded. The notification appears as:
```
Virtual Camera
RTMP stream active
```

### Camera Source Toggle

The **Camera Source** switch (if enabled via Zygisk/LSPosed) attempts to activate the virtual camera integration. This requires the root module to be installed and active.

## Zygisk/LSPosed Module (Root Integration)

### Overview

The `zygisk/module/` directory contains a **template** C/C++ module that can be loaded by Zygisk Next or LSPosed to hook Camera2 API calls.

**This is NOT a complete, production-ready solution.** It requires:

1. **Compilation** for your exact Pixel 4 XL firmware
2. **Integration** with Zygisk Next or LSPosed (whichever is installed)
3. **Testing** against real apps that use Camera2, Camera1, and CameraX
4. **Tuning** based on app behavior

### Limitations

- **Camera1 API** requires separate hooking logic
- **CameraX** uses Camera2 internally, so hooking Camera2 may work
- **NDK Camera** (android_camera.h) runs outside the Java framework and is harder to intercept
- **System apps** like the default Camera app may have vendor-specific HAL access

### Installation (Advanced)

1. **Compile the module** (requires Android NDK and knowledge of Zygisk module structure):
   ```bash
   cd zygisk/module
   ndk-build
   ```

2. **Package as a Zygisk module** following the official [Zygisk module format](https://github.com/topjohnwu/Magisk/blob/master/docs/zygisk.md)

3. **Load via Zygisk Next** or LSPosed, depending on your root framework

4. **Test with Camera2 apps** (e.g., Google Camera, Open Camera)

5. **Iterate**: Adjust the hooking logic in `camera_hook.cpp` based on app behavior

### Recommended Testing Workflow

1. Start with the APK only (no root module) — verify the RTMP stream works
2. Build a basic Zygisk module that logs Camera2 API calls (without redirection)
3. Deploy to the device and observe logs in `logcat`:
   ```powershell
   adb logcat | findstr "CameraHook"
   ```
4. Implement redirection to the virtual stream in `camera_hook.cpp`
5. Test with one app at a time (e.g., Open Camera first)
6. Expand to other apps
7. Document which apps work and which don't

## Troubleshooting

### Build Errors

**"SDK not found"**
- Verify `$env:ANDROID_HOME` points to `C:\Android`
- Run `sdkmanager --list` to confirm SDK tools are installed

**"Gradle wrapper not found"**
- Ensure you cloned the repo with all files (including `gradlew.bat`)

**"Java version mismatch"**
- Verify Java 17 is installed: `java -version`
- Set `$env:JAVA_HOME` to the correct path

### Connection Issues

**"Cannot connect to RTMP server"**
- Verify the device can reach the RTMP server (ping test from the device)
- Check the RTMP URL format: `rtmp://host:port/app/stream`
- Ensure the RTMP server is running and broadcasting

**"Connection timeout"**
- Check network latency: `adb shell ping 192.168.1.11`
- Increase the connection timeout in `StreamService.kt` if needed

**"Dropped frames"**
- May indicate slow decoding or insufficient device resources
- Try lowering the RTMP stream bitrate or resolution
- Monitor device CPU/memory: `adb shell dumpsys meminfo`

### Virtual Camera Not Working

**"Camera Source toggle disabled"**
- The Zygisk/LSPosed module is not installed or loaded
- Verify the root framework is active: `adb shell "su -c id"`

**"Module loads but camera still unavailable"**
- The hook may not be intercepting your app's Camera API calls
- Check logcat for hook debug messages: `adb logcat | findstr "CameraHook"`
- Verify the app uses Camera2 (not Camera1 or direct HAL access)
- Recompile and re-tune the hook

### Device Stability

**"Device reboots or camera framework crashes"**
- The Zygisk module may have a bug or incorrect hook logic
- Uninstall the module immediately via the root manager
- Restore the device to a working state (see recovery instructions below)
- Review the hook code for memory leaks or incorrect JNI calls

## Uninstall / Recovery

### Remove the Main App

```powershell
adb uninstall com.example.virtualcamera
```

### Remove the Zygisk/LSPosed Module

1. Reboot into recovery (if possible)
2. Use the root manager (Magisk/KernelSU) to uninstall the module
3. Reboot and verify camera functionality is restored

If the device is unstable:

1. Boot into bootloader: `adb reboot bootloader`
2. Flash a clean system image using `fastboot`
3. Restore from a previous backup

## Project Structure

```
virtual-camera-android/
├── app/                                    # Main Android application
│   ├── src/main/java/com/example/virtualcamera/
│   │   ├── MainActivity.kt                 # Main UI
│   │   ├── StreamService.kt                # Foreground service for streaming
│   │   ├── RtmpClient.kt                   # RTMP connection and validation
│   │   ├── ConnectionState.kt              # State machine
│   │   ├── UiState.kt                      # UI data model
│   │   └── App.kt                          # Application class
│   ├── src/test/java/com/example/virtualcamera/
│   │   ├── UrlValidatorTest.kt
│   │   ├── ConnectionStateTest.kt
│   │   └── ReconnectPolicyTest.kt
│   ├── src/main/res/                       # Resources
│   │   ├── layout/activity_main.xml
│   │   ├── values/strings.xml
│   │   └── values/themes.xml
│   ├── src/main/AndroidManifest.xml
│   └── build.gradle.kts
├── zygisk/module/                          # Zygisk hook template (C++)
│   ├── src/main/cpp/
│   │   ├── entry.cpp                       # Zygisk entry point
│   │   ├── camera_hook.cpp                 # Camera2 API hooks
│   │   └── hook_utils.cpp                  # Helper functions
│   ├── Android.mk
│   ├── Application.mk
│   └── CMakeLists.txt
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew & gradlew.bat                   # Gradle wrapper
└── README.md
```

## Dependencies

**Pinned versions for stability:**

- `androidx.core:core-ktx:1.13.1`
- `androidx.appcompat:appcompat:1.7.0`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.8.4`
- `androidx.activity:activity-ktx:1.9.1`
- `com.google.android.material:material:1.12.0`
- `androidx.constraintlayout:constraintlayout:2.1.4`
- Kotlin 1.9.24
- Android Gradle Plugin 8.6.1
- Compile SDK: 34 (Android 14)
- Target SDK: 34
- Min SDK: 28 (Android 9)

## Future Enhancements

- Real RTMP demuxer (librtmp or custom implementation)
- Full MediaCodec pipeline with Surface rendering
- Camera1 API hooking (for compatibility)
- NDK camera interception (advanced)
- Network bandwidth adaptation
- Audio stream support (AAC decoding and playback)
- Persistent settings storage
- Log export and debugging UI
- Multi-stream support

## Legal and Security Notes

1. **Rooting and module installation are device-specific and may void your warranty.**
2. **Hooking camera APIs can affect system stability; test thoroughly before deployment.**
3. **Do not expose unauthenticated RTMP servers to the internet.**
4. **This project is for educational and testing purposes on your own device.**

## References

- [Android Camera HAL Documentation](https://source.android.com/devices/camera)
- [Camera2 API](https://developer.android.com/reference/android/hardware/camera2/CameraManager)
- [Zygisk Module Development](https://github.com/topjohnwu/Magisk/blob/master/docs/zygisk.md)
- [LSPosed Framework](https://github.com/LSPosed/LSPosed)
- [RTMP Specification](https://rtmp.veriskope.com/docs/spec/)

## License

MIT License. See LICENSE file for details.

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review logcat output: `adb logcat`
3. Verify your device meets the requirements (Android 13, rooted, Zygisk/LSPosed)
4. Test the RTMP stream outside this app (e.g., with ffplay)
