package com.example.linksharecompose.utils

object ScreenRoute {
    val Home = Screen("home")
    val Community = Screen("community")
    val Search = Screen("search")
    val MyMemo = Screen("myMemo")
    val Settings = Screen("settings")
    val NicknameUpdate = Screen("nicknameUpdate")
    val Login = Screen("login")
    val Signup = Screen("signup")
    val NicknameSet = Screen("nicknameSet")
    val AppInfo = Screen("appInfo")

    data class Screen(val route: String)
}