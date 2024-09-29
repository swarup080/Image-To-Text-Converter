package com.example.imagetotext.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedImageDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savedData: SavedImageData)

    @Query("SELECT * FROM saved_image_data")
    fun getAllSavedData(): LiveData<List<SavedImageData>>

}