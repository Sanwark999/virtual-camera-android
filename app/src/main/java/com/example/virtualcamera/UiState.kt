package com.example.virtualcamera

data class UiState(
    val url: String = "rtmp://192.168.1.11:1935/live/server",
    val connected: Boolean = false,
    val status: String = "Disconnected",
    val resolution: String = "--",
    val fps: String = "--",
    val decoder: String = "N/A",
    val droppedFrames: Int = 0,
    val cameraEnabled: Boolean = false
)