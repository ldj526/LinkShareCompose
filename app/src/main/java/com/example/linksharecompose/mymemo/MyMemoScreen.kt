package com.example.linksharecompose.mymemo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.linksharecompose.utils.ScreenRoute

@Composable
fun MyMemoScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(ScreenRoute.MemoCreate.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "메모 작성")
            }
        }
    ) {
        // TODO(): LazyComlumn
    }
}