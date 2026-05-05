package com.mobile.t4mobile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mobile.t4mobile.R
import com.mobile.t4mobile.database.AppDatabase
import com.mobile.t4mobile.database.entity.StudentEntity
import com.mobile.t4mobile.databinding.FragmentDetailStudentBinding
import com.mobile.t4mobile.utils.DateUtil
import kotlinx.coroutines.launch

class DetailStudentFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudentBinding
    private lateinit var db: AppDatabase
    private var student: StudentEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        setupToolbar()
        setupListeners()
        loadStudentData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupListeners() {
        binding.btnEdit.setOnClickListener {
            student?.let {
                openFormFragment(it)
            }
        }
        binding.btnDelete.setOnClickListener {
            student?.let {
                showDeleteConfirmation(it)
            }
        }
    }

    private fun loadStudentData() {
        val studentId = arguments?.getInt("student_id") ?: return
        lifecycleScope.launch {
            student = db.studentDao().getStudentById(studentId)
            student?.let {
                displayStudent(it)
            }
        }
    }

    private fun displayStudent(student: StudentEntity) {
        binding.apply {
            tvName.text = student.name
            tvNim.text = "NIM: ${student.nim}"
            tvProdi.text = student.prodi
            tvSemester.text = "${student.semester}"
            tvEmail.text = student.email
            tvNotes.text = if (student.notes.isNotEmpty()) student.notes else "Tidak ada catatan"
            tvCreatedAt.text = "Dibuat: ${DateUtil.formatDate(student.createdAt)}"
            tvAvatarInitials.text = getInitials(student.name)
        }
    }

    private fun getInitials(name: String): String {
        val words = name.trim().split("\\s+".toRegex())
        return when {
            words.size >= 2 -> {
                "${words[0][0]}${words[1][0]}".uppercase()
            }
            words.isNotEmpty() && words[0].isNotEmpty() -> {
                words[0][0].toString().uppercase()
            }
            else -> "?"
        }
    }

    private fun showDeleteConfirmation(student: StudentEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus ${student.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteStudent(student)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteStudent(student: StudentEntity) {
        lifecycleScope.launch {
            db.studentDao().delete(student)
            Toast.makeText(requireContext(), "Data dihapus", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun openFormFragment(student: StudentEntity) {
        val fragment = FormStudentFragment().apply {
            arguments = Bundle().apply {
                putInt("student_id", student.id)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
