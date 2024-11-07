package com.example.linksharecompose.mymemo

import android.util.Log
import com.example.linksharecompose.auth.FirebaseCollection
import kotlinx.coroutines.tasks.await

class MemoRepository {

    // 메모 삭제
    suspend fun deleteMemo(memoId: String): Result<Unit> {
        return try {
            FirebaseCollection.memosCollection.document(memoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 메모 추가
    suspend fun addMemo(memo: Memo): Result<Unit> {
        return try {
            val docRef = FirebaseCollection.memosCollection.document()
            val memoId = memo.copy(memoId = docRef.id)
            docRef.set(memoId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 메모 가져오기
    suspend fun fetchMemos(): Result<List<Memo>> {
        return try {
            val querySnapshot = FirebaseCollection.memosCollection.get().await()
            val memoList = querySnapshot.documents.map { document ->
                Memo(
                    memoId = document.id,
                    uid = document.getString("uid"),
                    title = document.getString("title") ?: "",
                    content = document.getString("content") ?: "",
                    imageUri = document.getString("imageUri"),
                    linkUrl = document.getString("linkUrl") ?: "",
                    timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()
                )
            }
            Result.success(memoList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MemoId를 기반으로 메모 가져오기
    suspend fun fetchMemoById(memoId: String): Result<Memo> {
        return try {
            val document = FirebaseCollection.memosCollection.document(memoId).get().await()
            Log.d("MemoRepository", "document: $document")
            val memo = document.toObject(Memo::class.java)?.copy(memoId = document.id)
            Log.d("MemoRepository", "memo: $memo")
            memo?.let {
                Result.success(it)
            } ?: Result.failure(Exception("메모를 찾을 수 없습니다."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}