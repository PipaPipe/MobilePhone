package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val backButton = findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            // Простое закрытие
            finish()

            // Или с возвратом данных:
            /*
            val returnIntent = Intent()
            returnIntent.putExtra("result", "Данные")
            setResult(RESULT_OK, returnIntent)
            finish()
            */
        }
    }
}