package com.mobile.t4mobile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mobile.t4mobile.utils.SettingsManager

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val settingsManager = SettingsManager(newBase)
        val context = settingsManager.applySettings(newBase)
        super.attachBaseContext(context)
    }
}
