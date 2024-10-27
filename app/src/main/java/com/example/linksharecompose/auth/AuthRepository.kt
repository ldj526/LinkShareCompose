package com.example.linksharecompose.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    // 닉네임을 Firestore에 추가
    suspend fun addNicknameToUser(userId: String, nickname: String): Result<Unit> {
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