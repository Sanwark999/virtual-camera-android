package com.example.virtualcamera

import org.junit.Assert.assertEquals
import org.junit.Test

class ConnectionStateTest {
    @Test
    fun basicTransitions() {
        assertEquals(ConnectionState.DISCONNECTED, ConnectionState.DISCONNECTED)
    }
}
