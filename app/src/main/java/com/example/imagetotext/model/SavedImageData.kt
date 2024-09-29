package com.example.imagetotext.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_image_data")
data class SavedImageData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val imagePath: String
)
