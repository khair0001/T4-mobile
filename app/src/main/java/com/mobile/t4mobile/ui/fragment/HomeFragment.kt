package com.mobile.t4mobile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobile.t4mobile.R
import com.mobile.t4mobile.adapter.StudentAdapter
import com.mobile.t4mobile.adapter.SwipeToDeleteCallback
import com.mobile.t4mobile.database.AppDatabase
import com.mobile.t4mobile.database.entity.StudentEntity
import com.mobile.t4mobile.databinding.FragmentHomeBinding
import com.mobile.t4mobile.utils.BackupManager
import com.mobile.t4mobile.utils.LogManager
import com.mobile.t4mobile.utils.PreferencesManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase
    private lateinit var prefsManager: PreferencesManager
    private lateinit var logManager: LogManager
    private lateinit var backupManager: BackupManager
    private var studentList = emptyList<StudentEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        prefsManager = PreferencesManager(requireContext())
        logManager = LogManager(requireContext())
        backupManager = BackupManager(requireContext())

        setupRecyclerView()
        setupListeners()
        loadStudents()
        initializeSampleData()
        createDailyBackup()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onItemClick = { student -> openDetailFragment(student) },
            onEditClick = { student -> openFormFragment(student) },
            onDeleteClick = { student -> showDeleteConfirmation(student) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }

        // Setup Swipe to Delete
        val swipeCallback = object : SwipeToDeleteCallback(adapter, {}) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position >= 0 && position < studentList.size) {
                    val student = studentList[position]
                    deleteStudent(student)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            openFormFragment(null)
        }
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            studentList = db.studentDao().getAllStudents()
            updateUI()
        }
    }

    private fun updateUI() {
        adapter.updateData(studentList)
        val count = studentList.size
        binding.tvTotalStudent.text = count.toString()
        prefsManager.setStudentCount(count)

        if (studentList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun initializeSampleData() {
        lifecycleScope.launch {
            val count = db.studentDao().getStudentCount()
            if (count == 0) {
                val sampleData = listOf(
                    StudentEntity(
                        name = "Ahmad Muslihul Khair",
                        nim = "F1D02310001",
                        prodi = "Teknik Informatika",
                        email = "fufufafa@gmail.com",
                        semester = 6,
                        notes = "ingin jadi programer handal namun enggan ngoding"
                    ),
                    StudentEntity(
                        name = "Fufu fafa",
                        nim = "A1D02310011",
                        prodi = "Sistem Informasi",
                        email = "Fufufafa@example.com",
                        semester = 4,
                        notes = "Perlu bimbingan tambahan"
                    ),
                    StudentEntity(
                        name = "Alfarizi Uchiha",
                        nim = "F1D02310144",
                        prodi = "Teknik Informatika",
                        email = "uchiha@gmail.com",
                        semester = 6,
                        notes = ""
                    )
                )
                db.studentDao().insertAll(sampleData)
                logManager.logActivity("INIT", "Sample data initialized with ${sampleData.size} students")
                loadStudents()
            }
        }
    }

    private fun createDailyBackup() {
        lifecycleScope.launch {
            val students = db.studentDao().getAllStudents()
            val success = backupManager.createBackup(students)
            if (success) {
                logManager.logActivity("BACKUP", "Daily backup created with ${students.size} students")
            }
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
            logManager.logActivity("DELETE", "Student deleted: ${student.name} (${student.nim})")
            loadStudents()
            Snackbar.make(binding.root, "Data dihapus", Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    undoDelete(student)
                }
                .show()
        }
    }

    private fun undoDelete(student: StudentEntity) {
        lifecycleScope.launch {
            db.studentDao().insert(student)
            logManager.logActivity("UNDO", "Delete undo: ${student.name} (${student.nim})")
            loadStudents()
        }
    }

    private fun openFormFragment(student: StudentEntity?) {
        val fragment = FormStudentFragment().apply {
            arguments = Bundle().apply {
                if (student != null) {
                    putInt("student_id", student.id)
                }
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openDetailFragment(student: StudentEntity) {
        val fragment = DetailStudentFragment().apply {
            arguments = Bundle().apply {
                putInt("student_id", student.id)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        loadStudents()
    }
}
