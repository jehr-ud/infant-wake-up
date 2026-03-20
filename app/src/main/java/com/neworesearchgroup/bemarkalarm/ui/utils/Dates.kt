package com.neworesearchgroup.bemarkalarm.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val timeFormatter = SimpleDateFormat(
        "HH:mm:ss",
        Locale.getDefault()
    )

    private val dateTimeFormatter = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp))
    }
}