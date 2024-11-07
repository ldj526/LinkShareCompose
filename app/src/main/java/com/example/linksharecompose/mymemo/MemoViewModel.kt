package com.example.linksharecompose.mymemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MemoViewModel(private val repository: MemoRepository) : ViewModel() {

    private val _selectedMemos = MutableLiveData<Set<String>>(emptySet())
    val selectedMemos: LiveData<Set<String>> get() = _selectedMemos

    private val _isSelectionMode = MutableLiveData<Boolean>(false)
    val isSelectionMode: LiveData<Boolean> get() = _isSelectionMode

    private val _addMemoResult = MutableLiveData<Result<Unit>?>()
    val addMemoResult: LiveData<Result<Unit>?> get() = _addMemoResult

    private val _memos = MutableLiveData<List<Memo>>()
    val memos: LiveData<List<Memo>> get() = _memos

    private val _memo = MutableLiveData<Memo?>()
    val memo: LiveData<Memo?> get() = _memo

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 선택 관리
    fun selectMemo(memoId: String) {
        val currentSelections = _selectedMemos.value.orEmpty().toMutableSet()
        if (currentSelections.contains(memoId)) {
            currentSelections.remove(memoId)
        } else {
            currentSelections.add(memoId)
        }
        _selectedMemos.value = currentSelections
    }

    // 전체 선택/해제
    fun toggleSelectAllMemos() {
        val allMemoIds = _memos.value?.map { it.memoId }.orEmpty()
        if (_selectedMemos.value == allMemoIds.toSet()) {
            _selectedMemos.value = emptySet()
        } else {
            _selectedMemos.value = allMemoIds.toSet()
        }
    }

    // 선택된 메모 삭제
    fun deleteSelectedMemos() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val idsToDelete = _selectedMemos.value.orEmpty()
                for (memoId in idsToDelete) {
                    repository.deleteMemo(memoId)
                }
                _memos.value = _memos.value?.filterNot { idsToDelete.contains(it.memoId) }
                _selectedMemos.value = emptySet()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectionMode(enabled: Boolean) {
        _isSelectionMode.value = enabled
    }

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

    // MemoId를 기반으로 메모 가져오기
    fun fetchMemoById(memoId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.fetchMemoById(memoId)
                if (result.isSuccess) {
                    _memo.value = result.getOrNull()
                } else {
                    _memo.value = null
                }
            } catch (e: Exception) {
                _memo.value = null
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