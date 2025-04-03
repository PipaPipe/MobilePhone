package com.example.myapplication

import java.util.Date

// валюта
data class Currency(
    val ID: String,           // внутренний ID валюты
    val NumCode: String,      // цифровой код валюты
    val CharCode: String,     // буквенный код (USD, EUR и т.д.)
    val Nominal: Int,         // номинал (например, 1 доллар или 100 йен)
    val Name: String,         // название валюты на русском
    val Value: Double,        // иекущий курс
    val Previous: Double      // предыдущий курс
)

// ответ цб
data class CbrResponse(
    val Date: String,                  // дата обновления
    val PreviousDate: String,          // предыдущая дата обновления
    val PreviousURL: String,           // URL предыдущих данных
    val Timestamp: String,             // временная метка
    val Valute: Map<String, Currency>  // словарь валют
)

