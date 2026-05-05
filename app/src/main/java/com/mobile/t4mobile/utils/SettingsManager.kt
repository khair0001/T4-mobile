package com.mobile.t4mobile.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_NOTIF_ENABLED = "notif_enabled"
        private const val DEFAULT_FONT_SIZE = 14
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_MODE, enabled) }
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setFontSize(size: Int) {
        prefs.edit { putInt(KEY_FONT_SIZE, size) }
    }

    fun getFontSize(): Int {
        return prefs.getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NOTIF_ENABLED, enabled) }
    }

    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIF_ENABLED, true)
    }

    fun getFontScale(): Float {
        // Skala 1.0 adalah ukuran default (14).
        return getFontSize().toFloat() / DEFAULT_FONT_SIZE.toFloat()
    }

    fun applySettings(context: Context): Context {
        val config = Configuration(context.resources.configuration)
        config.fontScale = getFontScale()
        return context.createConfigurationContext(config)
    }
}
