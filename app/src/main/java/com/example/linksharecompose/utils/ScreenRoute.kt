package com.example.linksharecompose.utils

sealed class ScreenRoute(val route: String) {
    object Home : ScreenRoute("home")
    object Community : ScreenRoute("community")
    object Search : ScreenRoute("search")
    object MyMemo : ScreenRoute("myMemo")
    object Settings : ScreenRoute("settings")
    object NicknameUpdate : ScreenRoute("nicknameUpdate")
    object Login : ScreenRoute("login")
    object Signup : ScreenRoute("signup")
    object NicknameSet : ScreenRoute("nicknameSet")
    object AppInfo : ScreenRoute("appInfo")
    object MemoCreate : ScreenRoute("memoCreate")
    object MemoDetail : ScreenRoute("memo_detail/{memoId}") {
        fun createRoute(memoId: String) = "memo_detail/$memoId"
    }
}