package com.example.imagetotext.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.imagetotext.model.AppDatabase
import com.example.imagetotext.model.DataRepository
import com.example.imagetotext.model.SavedImageData
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DataRepository
    val allData: LiveData<List<SavedImageData>>

    init {
        val savedDataDao = AppDatabase.getDatabase(application).savedImageDataDao()
        repository = DataRepository(savedDataDao)
        allData = repository.allData
    }

    fun insert(savedData: SavedImageData) = viewModelScope.launch {
        repository.insert(savedData)
    }
}
