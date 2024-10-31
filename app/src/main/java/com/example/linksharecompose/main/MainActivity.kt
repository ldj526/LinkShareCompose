package com.example.linksharecompose.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.linksharecompose.auth.AuthActivity
import com.example.linksharecompose.auth.AuthRepository
import com.example.linksharecompose.settings.SettingsScreen
import com.example.linksharecompose.settings.SettingsViewModel
import com.example.linksharecompose.settings.SettingsViewModelFactory
import com.example.linksharecompose.ui.theme.LinkShareComposeTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkShareComposeTheme {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(
                        AuthRepository(
                            FirebaseAuth.getInstance()
                        )
                    )
                )
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController = navController) },
                    content = { paddingValues ->
                        NavigationComponent(navController, paddingValues, settingsViewModel)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Community,
        BottomNavScreen.Search,
        BottomNavScreen.MyMemo,
        BottomNavScreen.Settings
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(screen.title)
                    )
                },
                label = { Text(text = stringResource(id = screen.title)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationComponent(
    navController: NavHostController,
    paddingValues: PaddingValues,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavScreen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(BottomNavScreen.Home.route) {
            HomeScreen()
        }
        composable(BottomNavScreen.Community.route) {
            CommunityScreen()
        }
        composable(BottomNavScreen.Search.route) {
            SearchScreen()
        }
        composable(BottomNavScreen.MyMemo.route) {
            MyMemoScreen()
        }
        composable(BottomNavScreen.Settings.route) {
            SettingsScreen(navController,
                settingsViewModel,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    val context = navController.context
                    context.startActivity(Intent(context, AuthActivity::class.java))
                    (context as ComponentActivity).finish()
                })
        }
    }
}