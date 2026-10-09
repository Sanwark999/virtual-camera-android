package com.example.virtualcamera

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlValidatorTest {
    @Test
    fun validRtmpUrl() {
        assertTrue(UrlValidator.validate("rtmp://192.168.1.11:1935/live/server"))
    }

    @Test
    fun invalidMissingHost() {
        assertFalse(UrlValidator.validate("rtmp://"))
    }

    @Test
    fun invalidEmpty() {
        assertFalse(UrlValidator.validate(""))
    }
}
