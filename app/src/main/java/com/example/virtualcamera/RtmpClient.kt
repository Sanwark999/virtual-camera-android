package com.example.virtualcamera

import java.net.URI
import java.net.URISyntaxException
import kotlin.math.min

object UrlValidator {
    fun validate(raw: String): Boolean {
        val value = raw.trim()
        if (value.isEmpty()) return false
        return try {
            val uri = URI(value)
            uri.scheme != null &&
                (uri.scheme.equals("rtmp", true) || uri.scheme.equals("rtmps", true)) &&
                uri.host != null
        } catch (_: URISyntaxException) {
            false
        }
    }
}

data class ReconnectPolicy(
    val initialDelayMs: Long = 1000L,
    val maxDelayMs: Long = 30000L,
    val maxAttempts: Int = 10
) {
    fun nextDelay(attempt: Int): Long {
        val exp = (1L shl min(attempt, 5)) * initialDelayMs
        return min(exp, maxDelayMs)
    }
}