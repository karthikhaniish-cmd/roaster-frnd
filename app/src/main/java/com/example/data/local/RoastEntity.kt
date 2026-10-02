package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roasts")
data class RoastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetName: String,
    val description: String,
    val roastText: String,
    val language: String,
    val level: String,
    val style: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
