package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.*
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reloadButton = findViewById<Button>(R.id.reloadButton)
        val third = findViewById<Button>(R.id.thirdActivityButton)
        val vtext = findViewById<TextView>(R.id.act1_text)

        thread {
            try {
                val resp = sendRequest()
                runOnUiThread() {
                    val jsonString = resp
                    val cbrResponse = Gson().fromJson(jsonString, CbrResponse::class.java)

                    val currencyList = cbrResponse.Valute.values.toList()

                    vtext.text = "Актуально: \n${formatDateToRussian(cbrResponse.Date)}"

                    showCurrencies(currencyList)
                }
            } catch (e: Exception) {
                Log.e("TAG", e.message.toString())
                runOnUiThread {
                    vtext.text = "Ошибка: ${e.message}"
                }
            }
        }

        third.setOnClickListener {
            val i = Intent(this, ThirdActivity::class.java)
            startActivity(i)
        }
    }


    fun showCurrencies(currencies: List<Currency>) {
        val container = findViewById<LinearLayout>(R.id.currencyContainer)

        for (currency in currencies) {
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.currency_item, container, false)

            itemView.findViewById<TextView>(R.id.tvCharCode).text = "${currency.CharCode}"
            itemView.findViewById<TextView>(R.id.tvNominal).text = "${currency.Nominal}"
            itemView.findViewById<TextView>(R.id.tvName).text = "${currency.Name}"
            itemView.findViewById<TextView>(R.id.tvValue).text = "${currency.Value}"
            itemView.findViewById<TextView>(R.id.tvPrevious).text = "${currency.Previous}"


            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("ID", currency.ID)
                    putString("CharCode", currency.CharCode)
                    putInt("Nominal", currency.Nominal)
                    putString("Name", currency.Name)
                    putDouble("Value", currency.Value)
                    putDouble("Previous", currency.Previous)
                }

                val intent = Intent(this, SecondActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            container.addView(itemView)
        }
    }
}


fun sendRequest(): String {
    val url = URL("https://www.cbr-xml-daily.ru/daily_json.js")
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

fun formatDateToRussian(isoDate: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    val date = parser.parse(isoDate) // 2025-04-01T11:30:00+03:00 → Date


    val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("ru"))
    return formatter.format(date)
}


