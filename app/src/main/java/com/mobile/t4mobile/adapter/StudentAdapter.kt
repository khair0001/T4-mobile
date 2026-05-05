package com.mobile.t4mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.t4mobile.database.entity.StudentEntity
import com.mobile.t4mobile.databinding.ItemStudentBinding

class StudentAdapter(
    private var students: List<StudentEntity> = emptyList(),
    private val onItemClick: (StudentEntity) -> Unit,
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    fun updateData(newStudents: List<StudentEntity>) {
        students = newStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount() = students.size

    inner class StudentViewHolder(private val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: StudentEntity) {
            binding.apply {
                tvName.text = student.name
                tvNim.text = "NIM: ${student.nim}"
                tvProdi.text = student.prodi
                tvSemester.text = "Sem: ${student.semester}"
                tvAvatarInitials.text = getInitials(student.name)
                
                root.setOnClickListener { onItemClick(student) }
                btnEdit.setOnClickListener { onEditClick(student) }
                btnDelete.setOnClickListener { onDeleteClick(student) }
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
    }
}
