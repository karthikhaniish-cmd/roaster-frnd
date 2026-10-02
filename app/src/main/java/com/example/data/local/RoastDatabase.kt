package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoastEntity::class], version = 1, exportSchema = false)
abstract class RoastDatabase : RoomDatabase() {
    abstract fun roastDao(): RoastDao

    companion object {
        @Volatile
        private var INSTANCE: RoastDatabase? = null

        fun getInstance(context: Context): RoastDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoastDatabase::class.java,
                    "roast_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
