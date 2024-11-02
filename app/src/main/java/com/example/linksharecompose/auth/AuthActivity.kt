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
import com.example.linksharecompose.utils.ScreenRoute
import com.example.linksharecompose.nickname.NicknameRepository
import com.example.linksharecompose.nickname.NicknameSetScreen
import com.example.linksharecompose.nickname.NicknameViewModel
import com.example.linksharecompose.nickname.NicknameViewModelFactory
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
                val nicknameViewModel: NicknameViewModel = viewModel(
                    factory = NicknameViewModelFactory(
                        NicknameRepository()
                    )
                )
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        NavigationComponent(navController, paddingValues, authViewModel, nicknameViewModel)
                    })
            }
        }
    }
}

@Composable
fun NavigationComponent(
    navController: NavHostController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    nicknameViewModel: NicknameViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Login.route,
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        composable(ScreenRoute.Signup.route) { SignupScreen(navController, authViewModel, nicknameViewModel) }
        composable(ScreenRoute.Login.route) {
            LoginScreen(
                navController,
                authViewModel,
                nicknameViewModel,
                onLoginSuccess = {
                    val context = navController.context
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as ComponentActivity).finish()
                },
                onNavigateToNicknameSet = { userId ->
                    navController.navigate("${ScreenRoute.NicknameSet.route}/$userId")
                })
        }
        composable("${ScreenRoute.NicknameSet.route}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            NicknameSetScreen(navController, nicknameViewModel, userId, onNicknameSetSuccess = {
                val context = navController.context
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as ComponentActivity).finish()
            })
        }
    }
}