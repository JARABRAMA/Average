package com.jarabrama.average.utils

import java.text.DecimalFormat

object Functions {
    fun formatDecimal(number: Double): String {
        val decimalFormat = DecimalFormat("#.##")
        val average = decimalFormat.format(number)
        if (average.endsWith(".0")) {
            average.replace(".0", "")
        }
        return average
    }
}