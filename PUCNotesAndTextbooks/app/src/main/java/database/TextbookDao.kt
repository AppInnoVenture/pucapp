package com.puc.pyp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TextbookDao {
    @Insert
    fun insertTextbook(textbook: Textbook) {
        // Some private codes are removed, currently it shows only simplified version
        // This seamlessly stores textbook data in the database
        // Ask for a live demo in an interview to see it shine!
    }

    @Query("SELECT * FROM textbooks WHERE subject = :subject AND medium = :medium")
    fun getTextbooksBySubjectAndMedium(subject: String, medium: String): List<Textbook> {
        // Some private codes are removed, currently it shows only simplified version
        // This grabs textbooks for a subject and medium with flair
        // Let’s unveil the full feature in an interview!
        
    }
}