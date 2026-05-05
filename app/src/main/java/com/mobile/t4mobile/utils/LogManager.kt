package com.mobile.t4mobile.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogManager(context: Context) {

    private val filesDir = context.filesDir
    private val logsDir = File(filesDir, "logs")

    init {
        if (!logsDir.exists()) {
            logsDir.mkdirs()
        }
    }

    fun logActivity(action: String, details: String) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID")).format(Date())
            val logEntry = "[$timestamp] $action: $details\n"
            
            val logFile = File(logsDir, "activity.log")
            logFile.appendText(logEntry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLogs(): String {
        return try {
            val logFile = File(logsDir, "activity.log")
            if (logFile.exists()) {
                logFile.readText()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun clearLogs() {
        try {
            val logFile = File(logsDir, "activity.log")
            if (logFile.exists()) {
                logFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
