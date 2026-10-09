package com.example.virtualcamera

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private lateinit var urlEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var disconnectButton: Button
    private lateinit var statusText: TextView
    private lateinit var streamStats: TextView
    private lateinit var cameraToggle: Switch

    private val isConnected = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlEditText = findViewById(R.id.urlEditText)
        connectButton = findViewById(R.id.connectButton)
        disconnectButton = findViewById(R.id.disconnectButton)
        statusText = findViewById(R.id.statusText)
        streamStats = findViewById(R.id.streamStats)
        cameraToggle = findViewById(R.id.cameraToggle)

        connectButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (!UrlValidator.validate(url)) {
                statusText.text = "Invalid RTMP URL"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                connectToStream(url)
            }
        }

        disconnectButton.setOnClickListener {
            disconnectFromStream()
        }

        val serviceIntent = Intent(this, StreamService::class.java)
        startService(serviceIntent)
    }

    private fun connectToStream(url: String) {
        isConnected.set(true)
        statusText.text = "Connecting to $url..."
        streamStats.text = "Resolution: 1080x1920\nFPS: 60\nDecoder: MediaCodec\nDropped: 0"
    }

    private fun disconnectFromStream() {
        isConnected.set(false)
        statusText.text = "Disconnected"
        streamStats.text = "Resolution: --\nFPS: --\nDecoder: --\nDropped: 0"
    }
}