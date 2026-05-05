package com.mobile.t4mobile.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }
}
