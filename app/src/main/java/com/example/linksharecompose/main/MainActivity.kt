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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.linksharecompose.auth.AuthActivity
import com.example.linksharecompose.auth.AuthRepository
import com.example.linksharecompose.mymemo.MemoCreateScreen
import com.example.linksharecompose.mymemo.MemoDetailScreen
import com.example.linksharecompose.mymemo.MemoEditScreen
import com.example.linksharecompose.mymemo.MemoRepository
import com.example.linksharecompose.mymemo.MemoViewModel
import com.example.linksharecompose.mymemo.MemoViewModelFactory
import com.example.linksharecompose.mymemo.MyMemoScreen
import com.example.linksharecompose.nickname.NicknameRepository
import com.example.linksharecompose.nickname.NicknameUpdateScreen
import com.example.linksharecompose.nickname.NicknameViewModel
import com.example.linksharecompose.nickname.NicknameViewModelFactory
import com.example.linksharecompose.settings.AppInfoScreen
import com.example.linksharecompose.settings.SettingsScreen
import com.example.linksharecompose.settings.SettingsViewModel
import com.example.linksharecompose.settings.SettingsViewModelFactory
import com.example.linksharecompose.ui.theme.LinkShareComposeTheme
import com.example.linksharecompose.utils.ScreenRoute
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()

        setContent {
            LinkShareComposeTheme {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(
                        AuthRepository(auth), NicknameRepository()
                    )
                )
                val nicknameViewModel: NicknameViewModel = viewModel(
                    factory = NicknameViewModelFactory(
                        NicknameRepository()
                    )
                )
                val memoViewModel: MemoViewModel = viewModel(
                    factory = MemoViewModelFactory(
                        MemoRepository()
                    )
                )
                val navController = rememberNavController()

                val isSelectionMode by memoViewModel.isSelectionMode.observeAsState(false)

                // 현재 경로를 관찰하여 결정
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // 특정 화면에서만 BottomNavigationBar를 나타냄
                val shouldShowBottomBar = when (currentRoute) {
                    ScreenRoute.Search.route,
                    ScreenRoute.Settings.route,
                    ScreenRoute.MyMemo.route,
                    ScreenRoute.Home.route,
                    ScreenRoute.Community.route -> true
                    else -> false
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar && !isSelectionMode) {
                            BottomNavigationBar(navController = navController)
                    } },
                    content = { paddingValues ->
                        NavigationComponent(
                            navController,
                            paddingValues,
                            settingsViewModel,
                            nicknameViewModel,
                            memoViewModel,
                            auth
                        )
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
    settingsViewModel: SettingsViewModel,
    nicknameViewModel: NicknameViewModel,
    memoViewModel: MemoViewModel,
    auth: FirebaseAuth
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
            MyMemoScreen(navController, memoViewModel)
        }
        composable(BottomNavScreen.Settings.route) {
            SettingsScreen(navController,
                settingsViewModel, auth,
                onLogout = {
                    auth.signOut()
                    val context = navController.context
                    context.startActivity(Intent(context, AuthActivity::class.java))
                    (context as ComponentActivity).finish()
                })
        }
        composable(ScreenRoute.NicknameUpdate.route) {
            NicknameUpdateScreen(navController, nicknameViewModel)
        }
        composable(ScreenRoute.AppInfo.route) {
            AppInfoScreen(navController)
        }
        composable(ScreenRoute.MemoCreate.route) {
            MemoCreateScreen(navController, memoViewModel)
        }
        composable(
            ScreenRoute.MemoDetail.route, arguments = listOf(navArgument("memoId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val memoId = backStackEntry.arguments?.getString("memoId") ?: return@composable
            MemoDetailScreen(navController, memoViewModel, memoId)
        }
        composable(ScreenRoute.MemoEdit.route) {
            MemoEditScreen(navController, memoViewModel)
        }
    }
}