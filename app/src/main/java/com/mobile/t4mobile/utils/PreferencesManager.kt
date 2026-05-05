package com.mobile.t4mobile.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "StudentManagerPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_STUDENT_COUNT = "student_count"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    fun setStudentCount(count: Int) {
        prefs.edit().putInt(KEY_STUDENT_COUNT, count).apply()
    }

    fun getStudentCount(): Int {
        return prefs.getInt(KEY_STUDENT_COUNT, 0)
    }

    fun setFirstLaunch(isFirst: Boolean) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply()
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }
}
