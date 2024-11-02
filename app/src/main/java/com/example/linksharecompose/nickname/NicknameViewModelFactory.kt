package com.example.linksharecompose.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NicknameViewModelFactory(private val repository: NicknameRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NicknameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NicknameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}