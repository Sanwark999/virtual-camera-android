package com.example.virtualcamera

import org.junit.Assert.assertTrue
import org.junit.Test

class ReconnectPolicyTest {
    @Test
    fun exponentialBackoffBounded() {
        val policy = ReconnectPolicy()
        assertTrue(policy.nextDelay(0) <= 1000L)
        assertTrue(policy.nextDelay(5) <= policy.maxDelayMs)
    }
}
