package com.mobile.t4mobile.utils

import android.content.Context
import android.content.SharedPreferences

class AuthPrefManager(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    // ===== SIMPAN STATUS LOGIN =====
    fun saveLoginSession(username: String, password: String, rememberMe: Boolean) {
        sharedPref.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            apply()
        }
    }

    // ===== CEK STATUS LOGIN =====
    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isRememberMe(): Boolean {
        return sharedPref.getBoolean(KEY_REMEMBER_ME, false)
    }

    // ===== AMBIL DATA USER =====
    fun getUsername(): String {
        return sharedPref.getString(KEY_USERNAME, "") ?: ""
    }

    fun getPassword(): String {
        return sharedPref.getString(KEY_PASSWORD, "") ?: ""
    }

    // ===== LOGOUT (HAPUS SESSION) =====
    fun logout() {
        sharedPref.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            putBoolean(KEY_REMEMBER_ME, false)
            putString(KEY_USERNAME, "")
            putString(KEY_PASSWORD, "")
            apply()
        }
    }

    // ===== CLEAR REMEMBER ME =====
    fun clearRememberMe() {
        sharedPref.edit().apply {
            putBoolean(KEY_REMEMBER_ME, false)
            apply()
        }
    }
}
