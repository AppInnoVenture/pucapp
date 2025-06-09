package com.puc.pyp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert
    fun insertNote(note: Note) {
        // Some private codes are removed, currently it shows only simplified version
        // This handles the magic of saving notes to the database
        // Happy to demo the full wizardry in an interview!
    }

    @Query("SELECT * FROM notes WHERE subject = :subject")
    fun getNotesBySubject(subject: String): List<Note> {
        // Some private codes are removed, currently it shows only simplified version
        // This fetches notes for a given subject like a pro
        // Ready to showcase this in an interview demo!
        
    }
}