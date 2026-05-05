package com.mobile.t4mobile.utils

import android.util.Patterns

object ValidationUtil {

    fun validateInputs(
        name: String,
        nim: String,
        email: String,
        semester: String,
        prodi: String
    ): Boolean {
        return name.isNotBlank() &&
                nim.isNotBlank() &&
                email.isNotBlank() &&
                semester.isNotBlank() &&
                prodi.isNotBlank()
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
