package com.mobile.t4mobile.utils

import android.content.Context
import com.mobile.t4mobile.database.entity.StudentEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(context: Context) {

    private val filesDir = context.filesDir
    private val backupDir = File(filesDir, "backups")

    init {
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
    }

    fun createBackup(students: List<StudentEntity>): Boolean {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "students_backup_$timestamp.csv")

            val header = "ID,Name,NIM,Prodi,Email,Semester,Notes,CreatedAt\n"
            val content = students.joinToString("\n") {
                "${it.id},${it.name},${it.nim},${it.prodi},${it.email},${it.semester},${it.notes},${it.createdAt}"
            }

            backupFile.writeText(header + content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getLatestBackup(): File? {
        return backupDir.listFiles()?.maxByOrNull { it.lastModified() }
    }
}
