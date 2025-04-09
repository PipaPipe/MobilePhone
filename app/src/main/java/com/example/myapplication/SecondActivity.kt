package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.text.format
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val backButton = findViewById<Button>(R.id.backButton)
        val bundle = intent.extras

        // Находим View элементы
        val charCodeText = findViewById<TextView>(R.id.detailCharCode)
        val nominalText = findViewById<TextView>(R.id.detailNominal)
        val nameText = findViewById<TextView>(R.id.detailName)
        val valueText = findViewById<TextView>(R.id.detailValue)
        val previousText = findViewById<TextView>(R.id.detailPrevious)

        val ID = bundle?.get("ID")
        Log.d("TAG", ID.toString())

        var format = "W"
        val weekButton = findViewById<Button>(R.id.weekButton)
        val monthButton = findViewById<Button>(R.id.monthButton)
        val yearButton = findViewById<Button>(R.id.yearButton)

        val chart = findViewById<LineChart>(R.id.chart)
        weekButton.setOnClickListener {
            format = "W"
            thread {
                try {
                    val resp = sendRequestGraphics(ID.toString(), format)
                    Log.d("TAG", resp)
                    val (dates, floatValues) = parseCurrencyData(resp)
                    runOnUiThread {
                        setupChart(chart, dates, floatValues, "Динамика значений")
                    }
                } catch (e: Exception) {
                    Log.e("TAG", e.message.toString())

                }
            }
        }

        monthButton.setOnClickListener {
            format = "M"
            thread {
                try {
                    val resp = sendRequestGraphics(ID.toString(), format)
                    Log.d("TAG", resp)
                    val (dates, floatValues) = parseCurrencyData(resp)

                    runOnUiThread {
//                        testXML.text = resp
                        setupChart(chart, dates, floatValues, "Динамика значений")
                    }
                } catch (e: Exception) {
                    Log.e("TAG", e.message.toString())

                }
            }
        }

        yearButton.setOnClickListener {
            format = "Y"
            thread {
                try {
                    val resp = sendRequestGraphics(ID.toString(), format)
                    Log.d("TAG", resp)
                    val (dates, floatValues) = parseCurrencyData(resp)
                    runOnUiThread {
//                        testXML.text = resp
                        setupChart(chart, dates, floatValues, "Динамика значений")
                    }
                } catch (e: Exception) {
                    Log.e("TAG", e.message.toString())

                }
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        // Заполняем данные из Bundle
        bundle?.let {
            charCodeText.text = "Код: ${it.getString("CharCode", "")}"
            nominalText.text = "Номинал: ${it.getInt("Nominal", 1)}"
            nameText.text = "Название: ${it.getString("Name", "")}"
            valueText.text = "Текущий курс: ${it.getDouble("Value", 0.0)}"
            previousText.text = "Предыдущий курс: ${it.getDouble("Previous", 0.0)}"

        }
    }
}

fun sendRequestGraphics(id:String, format:String): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val date2 = dateFormat.format(calendar.time)

    if (format == "W") {
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
    } else if (format == "M") {
        calendar.add(Calendar.MONTH, -1)
    } else if (format == "Y"){
        calendar.add(Calendar.YEAR, -1)
    }

    val date1 = dateFormat.format(calendar.time)
    Log.d("TAG", "https://cbr.ru/scripts/XML_dynamic.asp?date_req1=${date1}&date_req2=${date2}&VAL_NM_RQ=${id}")
    val url = URL("https://cbr.ru/scripts/XML_dynamic.asp?date_req1=${date1}&date_req2=${date2}&VAL_NM_RQ=${id}")
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

fun parseCurrencyData(xmlData: String): Pair<List<String>, List<Float>> {
    val dates = mutableListOf<String>()
    val values = mutableListOf<Float>()

    val factory = XmlPullParserFactory.newInstance()
    factory.isNamespaceAware = true
    val parser = factory.newPullParser()
    parser.setInput(xmlData.reader())

    var eventType = parser.eventType
    var currentDate = ""
    var currentValue: Float? = null

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "Record" -> currentDate = parser.getAttributeValue(null, "Date")
                    "Value" -> {
                        val valueStr = parser.nextText().replace(",", ".")
                        currentValue = try {
                            valueStr.toFloat()
                        } catch (e: NumberFormatException) {
                            null
                        }
                    }
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "Record") {
                    if (currentDate.isNotEmpty() && currentValue != null) {
                        dates.add(currentDate)
                        values.add(currentValue)
                    }
                    currentDate = ""
                    currentValue = null
                }
            }
        }
        eventType = parser.next()
    }

    return Pair(dates, values)
}


fun setupChart(chart: LineChart, dates: List<String>, values: List<Float>, label: String) {
    // 1. Подготовка данных
    val entries = values.mapIndexed { index, value ->
        Entry(index.toFloat(), value)
    }

    // 2. Настройка линии
    val dataSet = LineDataSet(entries, label).apply {
        color = Color.BLUE
        valueTextColor = Color.BLACK
        lineWidth = 2f
        setDrawCircles(true)
        setCircleColor(Color.RED)
        valueTextSize = 10f
    }

    // 3. Настройка оси X
    chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        valueFormatter = IndexAxisValueFormatter(dates)
        labelCount = dates.size
        granularity = 1f
        setAvoidFirstLastClipping(true)
    }

    // 4. Общие настройки графика
    chart.apply {
        data = LineData(dataSet)
        description.text = "Динамика значений"
        axisRight.isEnabled = false
        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)
        setPinchZoom(true)
        animateY(1000)
        invalidate()
    }
}

