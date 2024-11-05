package com.example.linksharecompose.mymemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MemoViewModel(private val repository: MemoRepository) : ViewModel() {

    private val _addMemoResult = MutableLiveData<Result<Unit>?>()
    val addMemoResult: LiveData<Result<Unit>?> get() = _addMemoResult

    private val _memos = MutableLiveData<List<Memo>>()
    val memos: LiveData<List<Memo>> get() = _memos

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 메모 추가
    fun addMemo(memo: Memo) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.addMemo(memo)
                _addMemoResult.value = result
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 메모 가져오기
    fun fetchMemos() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.fetchMemos()
                if (result.isSuccess) {
                    _memos.value =
                        result.getOrNull()?.sortedByDescending { it.timestamp } ?: emptyList()
                } else {
                    _errorMessage.value = "메모를 가져오는데 실패했습니다."
                }
            } catch (e: Exception) {
                _errorMessage.value = "메모를 가져오는데 실패했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 메모 저장 성공 상태 초기화
    fun resetAddMemoResult() {
        _addMemoResult.value = null
    }

    // 에러 메세지 초기화
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}