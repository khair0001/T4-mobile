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
import com.mobile.t4mobile.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase
    private var studentList = emptyList<StudentEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        setupRecyclerView()
        setupSearchListener()
        loadAllStudents()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onItemClick = { student -> openDetailFragment(student) },
            onEditClick = { student -> openFormFragment(student) },
            onDeleteClick = { student -> showDeleteConfirmation(student) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
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

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchStudents(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun loadAllStudents() {
        lifecycleScope.launch {
            studentList = db.studentDao().getAllStudents()
            updateUI(studentList)
        }
    }

    private fun searchStudents(keyword: String) {
        lifecycleScope.launch {
            val results = if (keyword.isEmpty()) {
                db.studentDao().getAllStudents()
            } else {
                db.studentDao().searchStudents("%$keyword%")
            }
            studentList = results
            updateUI(results)
        }
    }

    private fun updateUI(results: List<StudentEntity>) {
        adapter.updateData(results)

        if (results.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
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
            searchStudents(binding.etSearch.text.toString())
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
            searchStudents(binding.etSearch.text.toString())
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
}
