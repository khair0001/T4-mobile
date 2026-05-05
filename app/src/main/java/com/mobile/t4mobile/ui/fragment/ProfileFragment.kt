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
import com.google.android.material.slider.Slider
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
        binding.tvUsername.text = authPrefManager.getUsername()
        binding.switchDarkMode.isChecked = settingsManager.isDarkMode()
        binding.switchNotif.isChecked = settingsManager.isNotificationEnabled()
        binding.sliderFontSize.value = settingsManager.getFontSize().toFloat()
    }

    private fun setupListeners() {
        // Dark Mode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Notifikasi
        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setNotificationEnabled(isChecked)
            Toast.makeText(requireContext(), if (isChecked) "Notifikasi Aktif" else "Notifikasi Mati", Toast.LENGTH_SHORT).show()
        }

        // Font Size Slider - Recreate activity saat selesai digeser agar berlaku global
        binding.sliderFontSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                settingsManager.setFontSize(slider.value.toInt())
                // Me-recreate activity untuk menerapkan skala font baru ke seluruh UI
                activity?.recreate()
            }
        })

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                authPrefManager.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
