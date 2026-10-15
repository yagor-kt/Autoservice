package com.example.autoservice.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyUtils {
    fun format(amount: Int): String {
        val symbols = DecimalFormatSymbols(Locale("ru")).apply {
            groupingSeparator = ' '
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0", symbols)
        return "${formatter.format(amount)} ₽"
    }
}