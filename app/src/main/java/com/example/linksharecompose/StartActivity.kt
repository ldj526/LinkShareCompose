package com.example.linksharecompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.example.linksharecompose.auth.AuthActivity
import com.example.linksharecompose.auth.FirebaseCollection
import com.google.firebase.auth.FirebaseAuth

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = FirebaseCollection.userCollection.document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nickname = document.getString("nickname")
                    if (nickname.isNullOrEmpty()) {
                        // 닉네임 존재할 때
                        startActivity(Intent(this, AuthActivity::class.java))
                    } else {
                        // 닉네임 존재하지 않을 때
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                } else {
                    // Firestore에 data가 없을 때
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                finish()
            }.addOnFailureListener {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        } else {
            // 로그인이 되어있지 않을 때
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}