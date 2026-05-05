package com.mobile.t4mobile.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobile.t4mobile.database.dao.StudentDao
import com.mobile.t4mobile.database.entity.StudentEntity

@Database(
    entities = [StudentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
