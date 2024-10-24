package com.example.linksharecompose.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

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

    // 이메일 중복 확인
    suspend fun checkEmailDuplication(email: String): Result<Boolean> {
        return try {
            val result = FirebaseCollection.userCollection
                .whereEqualTo("email", email)
                .get()
                .await()
            Result.success(result.isEmpty.not())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원가입
    suspend fun signupUserWithEmail(email: String, password: String, nickname: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            val userData = hashMapOf(
                "email" to email,
                "nickname" to nickname,
                "authProvider" to "email/password",
                "lastUpdated" to 0L
            )
            FirebaseCollection.userCollection.document(user.uid).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}