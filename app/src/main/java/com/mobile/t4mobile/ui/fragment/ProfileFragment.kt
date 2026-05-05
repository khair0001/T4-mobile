package com.mobile.t4mobile.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.mobile.t4mobile.LoginActivity
import com.mobile.t4mobile.databinding.FragmentProfileBinding
import com.mobile.t4mobile.utils.AuthPrefManager
import com.mobile.t4mobile.utils.SettingsManager

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authPrefManager: AuthPrefManager
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPrefManager = AuthPrefManager(requireContext())
        settingsManager = SettingsManager(requireContext())

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Tampilkan nama user yang login
        binding.tvUsername.text = authPrefManager.getUsername()

        // Set status switch dari SettingsManager
        binding.switchDarkMode.isChecked = settingsManager.isDarkMode()
        binding.switchNotif.isChecked = settingsManager.isNotificationEnabled()
    }

    private fun setupListeners() {
        // Switch Dark Mode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Switch Notifikasi
        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setNotificationEnabled(isChecked)
            val message = if (isChecked) "Notifikasi diaktifkan" else "Notifikasi dimatikan"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        // Membersihkan session
        authPrefManager.logout()
        Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

        // Kembali ke LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
