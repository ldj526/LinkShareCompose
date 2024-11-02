package com.example.linksharecompose.nickname

import com.example.linksharecompose.auth.FirebaseCollection
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class NicknameRepository {

    // 닉네임을 Firestore에 추가
    suspend fun setNickname(userId: String, nickname: String): Result<Unit> {
        return try {
            val userRef = FirebaseCollection.userCollection.document(userId)
            userRef.update("nickname", nickname).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 구글로 로그인 했을 때 닉네임 존재 여부
    suspend fun userHasNickname(user: FirebaseUser): Boolean {
        val userRef = FirebaseCollection.userCollection.document(user.uid)
        val snapshot = userRef.get().await()
        return snapshot.getString("nickname") != null
    }

    // 닉네임 가져오기
    suspend fun getNickname(userId: String): Result<String?> {
        return try {
            val userRef = FirebaseCollection.userCollection.document(userId)
            val snapshot = userRef.get().await()
            val nickname = snapshot.getString("nickname")
            Result.success(nickname)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 닉네임 중복 확인
    suspend fun checkNicknameDuplication(nickname: String): Result<Boolean> {
        return try {
            val result = FirebaseCollection.userCollection
                .whereEqualTo("nickname", nickname)
                .get()
                .await()
            Result.success(result.isEmpty.not())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}