package com.puc.pyp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "textbooks")
data class Textbook(
    @PrimaryKey val id: Int,
    val subject: String,
    val medium: String, // e.g., "English" or "Kannada"
    val filePath: String
)