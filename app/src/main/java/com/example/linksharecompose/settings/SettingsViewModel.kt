package com.example.linksharecompose.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linksharecompose.auth.AuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail

    private val _authProvider = MutableLiveData<String?>()
    val authProvider: LiveData<String?> get() = _authProvider

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _userEmail.value = repository.getCurrentUserEmail()
                _authProvider.value = repository.getCurrentUserProvider()
            } finally {
                _isLoading.value = false
            }
        }
    }
}