package com.example.linksharecompose.auth

import com.example.linksharecompose.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    // 비밀번호를 통한 사용자 재인증
    suspend fun reauthenticateUserWithPassword(password: String): Result<Unit> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val email = currentUser.email ?: return Result.failure(Exception("이메일 정보를 가져올 수 없습니다."))
            val credential = EmailAuthProvider.getCredential(email, password)
            return try {
                currentUser.reauthenticate(credential).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("${R.string.not_logged}"))
        }
    }

    // 회원 탈퇴
    suspend fun deleteUser(): Result<Unit> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = FirebaseCollection.userCollection.document(userId)
            try {
                // Firestore 문서 삭제
                userRef.delete().await()
                // Firebase Authentication 계정 삭제
                currentUser.delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("${R.string.not_logged}"))
        }
    }

    // 구글로 로그인
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: throw Exception("Google Sign-In failed")
            val userRef = FirebaseCollection.userCollection.document(user.uid)

            val snapshot = userRef.get().await()
            if (!snapshot.exists()) {
                val userData = hashMapOf(
                    "email" to user.email,
                    "nickname" to null,
                    "authProvider" to "Google",
                    "lastUpdated" to 0L
                )
                userRef.set(userData, SetOptions.merge()).await()
            }

            Result.success(authResult.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Email/Password 로그인
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult.user)
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
                "authProvider" to "Email/Password",
                "lastUpdated" to 0L
            )
            FirebaseCollection.userCollection.document(user.uid).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}