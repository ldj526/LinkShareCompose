package com.example.linksharecompose.main

object ScreenRoute {
    val Home = Screen("home")
    val Community = Screen("community")
    val Search = Screen("search")
    val MyMemo = Screen("myMemo")
    val Settings = Screen("settings")

    data class Screen(val route: String)
}