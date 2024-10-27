package com.example.linksharecompose.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _signupResult = MutableLiveData<Result<Unit>>()
    val signupResult: LiveData<Result<Unit>> get() = _signupResult

    private val _emailStatus = MutableLiveData<String?>(null)
    val emailStatus: LiveData<String?> = _emailStatus

    private val _isEmailAvailable = MutableLiveData<Boolean?>(null)
    val isEmailAvailable: LiveData<Boolean?> = _isEmailAvailable

    private val _nicknameStatus = MutableLiveData<String?>(null)
    val nicknameStatus: LiveData<String?> = _nicknameStatus

    private val _nicknameAddStatus = MutableLiveData<Result<Unit>>()
    val nicknameAddStatus: LiveData<Result<Unit>> get() = _nicknameAddStatus

    private val _isNicknameAvailable = MutableLiveData<Boolean?>(null)
    val isNicknameAvailable: LiveData<Boolean?> = _isNicknameAvailable

    private val _passwordStatus = MutableLiveData<String?>(null)
    val passwordStatus: LiveData<String?> = _passwordStatus

    private val _confirmPasswordStatus = MutableLiveData<String?>(null)
    val confirmPasswordStatus: LiveData<String?> = _confirmPasswordStatus

    private val _isSignupLoading = MutableLiveData<Boolean>(false)
    val isSignupLoading: LiveData<Boolean> = _isSignupLoading

    private val _isNicknameCheckLoading = MutableLiveData<Boolean>(false)
    val isNicknameCheckLoading: LiveData<Boolean> = _isNicknameCheckLoading

    private val _isLoginLoading = MutableLiveData<Boolean>(false)
    val isLoginLoading: LiveData<Boolean> = _isLoginLoading

    private val _emailLoginResult = MutableLiveData<Result<FirebaseUser?>>()
    val emailLoginResult: LiveData<Result<FirebaseUser?>> get() = _emailLoginResult

    private val _googleLoginResult = MutableLiveData<Result<FirebaseUser?>>()
    val googleLoginResult: LiveData<Result<FirebaseUser?>> get() = _googleLoginResult

    // 닉네임을 Firestore에 추가
    fun addNickname(userId: String, nickname: String) {
        _isSignupLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.addNicknameToUser(userId, nickname)
                _nicknameAddStatus.postValue(result)
            } finally {
                _isSignupLoading.value = false
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

    // Google로 로그인
    fun signInWithGoogle(idToken: String) {
        _isLoginLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogle(idToken)
                _googleLoginResult.postValue(result)
            } finally {
                _isLoginLoading.value = false
            }
        }
    }

    // 로그인
    fun signInWithEmailAndPassword(email: String, password: String) {
        _isLoginLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.signInWithEmailAndPassword(email, password)
                _emailLoginResult.postValue(result)
            } finally {
                _isLoginLoading.value = false
            }
        }
    }

    // 이메일 중복 확인
    private fun checkEmailDuplication(email: String) {
        viewModelScope.launch {
            try {
                val result = repository.checkEmailDuplication(email)
                result.fold(
                    onSuccess = { isDuplicated ->
                        val isAvailable = !isDuplicated
                        _isEmailAvailable.value = isAvailable
                        _emailStatus.value =
                            if (isAvailable) "사용 가능한 이메일입니다." else "이미 사용 중인 이메일입니다."
                    },
                    onFailure = {
                        _isEmailAvailable.value = false
                        _emailStatus.value = "이메일 중복 확인에 실패했습니다."
                    }
                )
            } catch (e: Exception) {
                _isEmailAvailable.value = false
                _emailStatus.value = "이메일 중복 확인에 실패했습니다."
            }
        }
    }

    // 이메일 입력 시 UI 변경
    fun onEmailChanged(email: String) {
        if (email.isNotBlank()) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailStatus.value = null
                checkEmailDuplication(email)
            } else {
                _emailStatus.value = "이메일 형식이 잘못됐습니다."
                _isEmailAvailable.value = false
            }
        } else {
            _emailStatus.value = null
            _isEmailAvailable.value = null
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
                        _isEmailAvailable.value = false
                        _emailStatus.value = "닉네임 중복 확인에 실패했습니다."
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

    // 비밀번호 입력 시 UI 변경
    fun onPasswordChanged(password: String, confirmPassword: String) {
        val pwdPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]+$"
        _passwordStatus.value = when {
            password.isBlank() -> "비밀번호를 입력해주세요."
            password.length < 8 || password.length > 20 -> "8자리 이상 20자리 이하로 입력해주세요"
            !password.matches(pwdPattern.toRegex()) -> "영문, 숫자, 특수문자를 1개 이상 입력해주세요."
            else -> "사용 가능한 비밀번호입니다."
        }
        _confirmPasswordStatus.value = when {
            confirmPassword.isBlank() -> "비밀번호를 입력해주세요."
            confirmPassword != password -> "비밀번호가 일치하지 않습니다."
            else -> "비밀번호가 일치합니다."
        }
    }

    // 회원가입
    fun signupUserWithEmail(email: String, password: String, nickname: String) {
        _isSignupLoading.value = true
        viewModelScope.launch {
            try {
                _signupResult.value = repository.signupUserWithEmail(email, password, nickname)
            } finally {
                _isSignupLoading.value = false
            }

        }
    }
}