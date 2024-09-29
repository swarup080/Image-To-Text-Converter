package com.example.imagetotext.model

import androidx.lifecycle.LiveData

class DataRepository(private val savedDataDao: SavedImageDataDao) {
    val allData: LiveData<List<SavedImageData>> = savedDataDao.getAllSavedData()

    suspend fun insert(savedData: SavedImageData) {
        savedDataDao.insert(savedData)
    }
}
