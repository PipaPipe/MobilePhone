package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import android.widget.Button
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

//import androidx.appcompat.app.AppCompatActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val startButton = findViewById<Button>(R.id.startButton)
        val vtext = findViewById<TextView>(R.id.act1_text)

        val t = object: Thread(){
            override fun run(){

            }
        }

        t.start()

        startButton.setOnClickListener {
            vtext.text = "Загрузка..."
            // Действие при нажатии на кнопку
            thread {
                try {
                    val resp = sendRequest()
                    runOnUiThread {
                        vtext.text = resp
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        vtext.text = "Ошибка: ${e.message}"
                    }
                }
            }
            val i = Intent(this, SecondActivity::class.java)
            startActivity(i)
        }
    }
}


fun sendRequest(): String {
    val url = URL("https://jsonplaceholder.typicode.com/posts/1")
    val connection = url.openConnection() as HttpURLConnection

    return try {
        connection.apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
        }

        when (connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                connection.inputStream.bufferedReader().use { it.readText() }
            }
            else -> {
                "HTTP Error: ${connection.responseCode}"
            }
        }
    } finally {
        connection.disconnect()
    }
}
