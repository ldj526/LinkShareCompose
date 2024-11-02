package com.example.linksharecompose.nickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class NicknameViewModel(private val repository: NicknameRepository) : ViewModel() {

    private val _nicknameSetStatus = MutableLiveData<Result<Unit>?>()
    val nicknameSetStatus: LiveData<Result<Unit>?> get() = _nicknameSetStatus

    private val _nicknameStatus = MutableLiveData<String?>(null)
    val nicknameStatus: LiveData<String?> = _nicknameStatus

    private val _isNicknameAvailable = MutableLiveData<Boolean?>(null)
    val isNicknameAvailable: LiveData<Boolean?> = _isNicknameAvailable

    private val _isNicknameCheckLoading = MutableLiveData<Boolean>(false)
    val isNicknameCheckLoading: LiveData<Boolean> = _isNicknameCheckLoading

    private val _setNicknameLoading = MutableLiveData<Boolean>(false)
    val setNicknameLoading: LiveData<Boolean> = _setNicknameLoading

    // 닉네임을 Firestore에 추가
    fun setNickname(userId: String, nickname: String) {
        _setNicknameLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.setNickname(userId, nickname)
                _nicknameSetStatus.postValue(result)
            } finally {
                _setNicknameLoading.value = false
            }
        }
    }

    // Nickname의 존재 여부 확인 후 view 이동
    fun checkAndNavigateToNicknameScreen(
        user: FirebaseUser,
        onNavigateToNicknameSet: (String) -> Unit,
        onLoginSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val hasNickname = repository.userHasNickname(user)
            if (!hasNickname) {
                onNavigateToNicknameSet(user.uid)
            } else {
                onLoginSuccess()
            }
        }
    }

    // 닉네임 중복 확인
    fun checkNicknameDuplication(nickname: String) {
        _isNicknameCheckLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.checkNicknameDuplication(nickname)
                result.fold(
                    onSuccess = { isDuplicated ->
                        val isAvailable = !isDuplicated
                        _isNicknameAvailable.value = isAvailable
                        _nicknameStatus.value = if (isAvailable) "사용 가능한 닉네임입니다." else "중복된 닉네임입니다."
                    },
                    onFailure = {
                        _isNicknameAvailable.value = false
                        _nicknameStatus.value = "닉네임 중복 확인에 실패했습니다."
                    }
                )
            } catch (e: Exception) {
                _isNicknameAvailable.value = false
                _nicknameStatus.value = "닉네임 중복 확인에 실패했습니다."
            } finally {
                _isNicknameCheckLoading.value = false
            }
        }
    }

    // 닉네임 입력 시 UI 변경
    fun onNicknameChanged(nickname: String) {
        val nicknamePattern = "^[A-Za-z0-9가-힣]{2,10}$"
        if (nickname.isBlank()) {
            _nicknameStatus.value = null
            _isNicknameAvailable.value = null
        } else if (!nickname.matches(nicknamePattern.toRegex())) {
            _nicknameStatus.value = "닉네임은 한글, 영어, 숫자만 가능합니다.\n2자 이상 10자 이하로 입력해주세요."
            _isNicknameAvailable.value = false
        } else {
            _nicknameStatus.value = null
            _isNicknameAvailable.value = null
        }
    }

    fun resetNicknameCheckStatus() {
        _nicknameStatus.value = null
        _isNicknameAvailable.value = null
    }

    fun resetNicknameUpdateResult() {
        _nicknameSetStatus.value = null
    }
}