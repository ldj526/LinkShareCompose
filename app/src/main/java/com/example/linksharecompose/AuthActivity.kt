package com.example.linksharecompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.linksharecompose.ui.theme.LinkShareComposeTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkShareComposeTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        NavigationComponent(navController, paddingValues)
                    })
            }
        }
    }
}

@Composable
fun NavigationComponent(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "loginScreen",
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        composable("signupScreen") { SignupScreen(navController) }
        composable("loginScreen") {
            LoginScreen(
                navController,
                onLoginSuccess = {
                    val context = navController.context
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as ComponentActivity).finish()
                })
        }
    }
}