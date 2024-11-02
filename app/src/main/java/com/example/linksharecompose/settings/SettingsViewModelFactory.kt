package com.example.linksharecompose.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linksharecompose.auth.AuthRepository
import com.example.linksharecompose.nickname.NicknameRepository

class SettingsViewModelFactory(private val authRepository: AuthRepository, private val nicknameRepository: NicknameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(authRepository, nicknameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}