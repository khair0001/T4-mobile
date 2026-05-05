package com.mobile.t4mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.mobile.t4mobile.utils.AuthPrefManager

class LoginActivity : AppCompatActivity() {

    private lateinit var authPrefManager: AuthPrefManager
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authPrefManager = AuthPrefManager(this)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Check if already logged in
        if (authPrefManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        // Load credentials if previously saved
        if (authPrefManager.isRememberMe()) {
            etUsername.setText(authPrefManager.getUsername())
            etPassword.setText(authPrefManager.getPassword())
        }

        // Setup login button
        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Simple validation: admin/admin
        if (username == "admin" && password == "admin") {
            // Kita set rememberMe ke true secara default atau sesuai kebutuhan logic Anda
            authPrefManager.saveLoginSession(username, password, true)
            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
            navigateToMain()
        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
