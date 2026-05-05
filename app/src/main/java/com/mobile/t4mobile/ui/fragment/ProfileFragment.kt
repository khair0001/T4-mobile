package com.mobile.t4mobile.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.mobile.t4mobile.LoginActivity
import com.mobile.t4mobile.R
import com.mobile.t4mobile.databinding.FragmentProfileBinding
import com.mobile.t4mobile.utils.AuthPrefManager

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authPrefManager: AuthPrefManager

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

        // Set username
        binding.tvUsername.text = authPrefManager.getUsername()

        // Setup logout button
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
        authPrefManager.logout()
        Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
