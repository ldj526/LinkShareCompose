package com.example.linksharecompose.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.linksharecompose.R
import com.example.linksharecompose.utils.ScreenRoute

sealed class BottomNavScreen(val route: String, @StringRes val title: Int, val icon: ImageVector) {
    object Home : BottomNavScreen(ScreenRoute.Home.route, R.string.Home, Icons.Default.Home)
    object Community : BottomNavScreen(ScreenRoute.Community.route, R.string.Community, Icons.Default.Dashboard)
    object Search : BottomNavScreen(ScreenRoute.Search.route, R.string.Search, Icons.Default.Search)
    object MyMemo : BottomNavScreen(ScreenRoute.MyMemo.route, R.string.MyMemo, Icons.Default.Edit)
    object Settings : BottomNavScreen(ScreenRoute.Settings.route, R.string.Settings, Icons.Default.Settings)
}