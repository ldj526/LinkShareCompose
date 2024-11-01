package com.example.linksharecompose.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linksharecompose.auth.AuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _deleteAccountStatus = MutableLiveData<Result<Unit>?>()
    val deleteAccountStatus: LiveData<Result<Unit>?> get() = _deleteAccountStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun deleteAccount(password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val reauthResult = repository.reauthenticateUserWithPassword(password)
                if (reauthResult.isSuccess) {
                    val deleteResult = repository.deleteUser()
                    _deleteAccountStatus.value = deleteResult
                } else {
                    _deleteAccountStatus.postValue(Result.failure(reauthResult.exceptionOrNull()!!))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetDeleteAccountStatus() {
        _deleteAccountStatus.value = null
    }
}