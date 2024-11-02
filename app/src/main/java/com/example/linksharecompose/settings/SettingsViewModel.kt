package com.example.linksharecompose.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linksharecompose.auth.AuthRepository
import com.example.linksharecompose.nickname.NicknameRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val authRepository: AuthRepository, private val nicknameRepository: NicknameRepository) : ViewModel() {

    private val _deleteAccountStatus = MutableLiveData<Result<Unit>?>()
    val deleteAccountStatus: LiveData<Result<Unit>?> get() = _deleteAccountStatus

    private val _getNicknameStatus = MutableLiveData<Result<String?>>()
    val getNicknameStatus: LiveData<Result<String?>> get() = _getNicknameStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isNicknameLoading = MutableLiveData<Boolean>()
    val isNicknameLoading: LiveData<Boolean> get() = _isNicknameLoading

    // 닉네임 가져오기
    fun getNickname(userId: String) {
        _isNicknameLoading.value = true
        viewModelScope.launch {
            try {
                val result = nicknameRepository.getNickname(userId)
                _getNicknameStatus.value = result
            } finally {
                _isNicknameLoading.value = false
            }
        }
    }

    // 계정 삭제
    fun deleteAccount(password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val reauthResult = authRepository.reauthenticateUserWithPassword(password)
                if (reauthResult.isSuccess) {
                    val deleteResult = authRepository.deleteUser()
                    _deleteAccountStatus.value = deleteResult
                } else {
                    _deleteAccountStatus.postValue(Result.failure(reauthResult.exceptionOrNull()!!))
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 계정 상태 리셋
    fun resetDeleteAccountStatus() {
        _deleteAccountStatus.value = null
    }
}