package com.puc.pyp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: Int,
    val subject: String,
    val chapter: String,
    val content: String
)
