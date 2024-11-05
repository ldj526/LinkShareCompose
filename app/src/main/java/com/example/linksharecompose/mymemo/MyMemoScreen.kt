package com.example.linksharecompose.mymemo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.linksharecompose.utils.ScreenRoute

@Composable
fun MyMemoScreen(navController: NavController, memoViewModel: MemoViewModel) {

    val memos by memoViewModel.memos.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        memoViewModel.fetchMemos()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(ScreenRoute.MemoCreate.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "메모 작성")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(memos) { memo ->
                    MemoItem(memo = memo)
                }
            }
        }
    }
}