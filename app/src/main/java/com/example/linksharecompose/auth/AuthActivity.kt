package com.example.linksharecompose.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.linksharecompose.main.MainActivity
import com.example.linksharecompose.ui.theme.LinkShareComposeTheme
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkShareComposeTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(
                        AuthRepository(
                            FirebaseAuth.getInstance()
                        )
                    )
                )
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        NavigationComponent(navController, paddingValues, authViewModel)
                    })
            }
        }
    }
}

@Composable
fun NavigationComponent(
    navController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "loginScreen",
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        composable("signupScreen") { SignupScreen(navController, authViewModel) }
        composable("loginScreen") {
            LoginScreen(
                navController,
                authViewModel,
                onLoginSuccess = {
                    val context = navController.context
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as ComponentActivity).finish()
                },
                onNavigateToNicknameSet = { userId ->
                    navController.navigate("nicknameSetScreen/$userId")
                })
        }
        composable("nicknameSetScreen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            NicknameSetScreen(navController, authViewModel, userId, onNicknameSetSuccess = {
                val context = navController.context
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as ComponentActivity).finish()
            })
        }
    }
}