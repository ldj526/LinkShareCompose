package com.example.linksharecompose.mymemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MemoViewModelFactory(private val repository: MemoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}