package com.mobile.t4mobile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mobile.t4mobile.R
import com.mobile.t4mobile.database.AppDatabase
import com.mobile.t4mobile.database.entity.StudentEntity
import com.mobile.t4mobile.databinding.FragmentFormStudentBinding
import com.mobile.t4mobile.utils.LogManager
import com.mobile.t4mobile.utils.ValidationUtil
import kotlinx.coroutines.launch

class FormStudentFragment : Fragment() {

    private lateinit var binding: FragmentFormStudentBinding
    private lateinit var db: AppDatabase
    private lateinit var logManager: LogManager
    private var studentId: Int? = null
    private var isEditMode = false

    private val prodis = arrayOf(
        "Teknik Informatika",
        "Sistem Informasi",
        "Teknik Komputer",
        "Manajemen Informatika",
        "Keamanan Siber"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        logManager = LogManager(requireContext())

        setupToolbar()
        setupProdiSpinner()
        setupListeners()

        studentId = arguments?.getInt("student_id")
        if (studentId != null && studentId != 0) {
            isEditMode = true
            loadStudentData()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupProdiSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, prodis)
        binding.spinnerProdi.setAdapter(adapter)
        // Memastikan dropdown muncul saat diklik meskipun inputType="none"
        binding.spinnerProdi.setOnClickListener {
            binding.spinnerProdi.showDropDown()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveStudent()
        }
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadStudentData() {
        lifecycleScope.launch {
            studentId?.let {
                val student = db.studentDao().getStudentById(it)
                student?.let {
                    binding.apply {
                        etName.setText(it.name)
                        etNim.setText(it.nim)
                        etEmail.setText(it.email)
                        etSemester.setText(it.semester.toString())
                        // Gunakan filter: false agar daftar dropdown tidak hilang saat teks diatur
                        spinnerProdi.setText(it.prodi, false)
                        etNotes.setText(it.notes)
                        toolbar.title = "Edit Mahasiswa"
                        btnSave.text = "Update"
                    }
                }
            }
        }
    }

    private fun saveStudent() {
        val name = binding.etName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val semester = binding.etSemester.text.toString().trim()
        val prodi = binding.spinnerProdi.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (!ValidationUtil.validateInputs(name, nim, email, semester, prodi)) {
            Toast.makeText(requireContext(), "Semua field harus diisi dengan benar", Toast.LENGTH_SHORT).show()
            return
        }

        if (!ValidationUtil.isValidEmail(email)) {
            Toast.makeText(requireContext(), "Format email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val semesterInt = semester.toIntOrNull() ?: 0
        if (semesterInt < 1 || semesterInt > 8) {
            Toast.makeText(requireContext(), "Semester harus antara 1-8", Toast.LENGTH_SHORT).show()
            return
        }

        val student = StudentEntity(
            id = studentId ?: 0,
            name = name,
            nim = nim,
            email = email,
            semester = semesterInt,
            prodi = prodi,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )

        lifecycleScope.launch {
            if (isEditMode) {
                db.studentDao().update(student)
                logManager.logActivity("UPDATE", "Student updated: $name ($nim)")
                Toast.makeText(requireContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                db.studentDao().insert(student)
                logManager.logActivity("CREATE", "Student created: $name ($nim)")
                Toast.makeText(requireContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
            parentFragmentManager.popBackStack()
        }
    }
}
