package com.example.linksharecompose.mymemo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.linksharecompose.utils.BottomActionBar
import com.example.linksharecompose.utils.ScreenRoute

@Composable
fun MyMemoScreen(navController: NavController, memoViewModel: MemoViewModel) {

    val memos by memoViewModel.memos.observeAsState(emptyList())
    val selectedMemos by memoViewModel.selectedMemos.observeAsState(emptySet())
    val isSelectionMode by memoViewModel.isSelectionMode.observeAsState(false)

    BackHandler(isSelectionMode) {
        memoViewModel.setSelectionMode(false)
    }

    LaunchedEffect(Unit) {
        memoViewModel.fetchMemos()
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "메모",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(2f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )

                    if (isSelectionMode) {
                        IconButton(onClick = {
                            memoViewModel.toggleSelectAllMemos()
                        }) {
                            Icon(
                                imageVector = if (selectedMemos.size == memos.size) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = if (selectedMemos.size == memos.size) "전체 해제" else "전체 선택"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            memoViewModel.setSelectionMode(true)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(ScreenRoute.MemoCreate.route)
                    },
                ) {
                    Icon(Icons.Default.Add, contentDescription = "메모 작성")
                }
            }
        },
        bottomBar = {
            if (isSelectionMode && selectedMemos.isNotEmpty()) {
                BottomActionBar(
                    onConfirm = {
                        memoViewModel.deleteSelectedMemos()
                        memoViewModel.setSelectionMode(false)
                    },
                    onCancel = {
                        memoViewModel.setSelectionMode(false)
                        memoViewModel.toggleSelectAllMemos()
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (memoViewModel.isLoading.value == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(memos) { memo ->
                        MemoItem(
                            memo = memo,
                            isSelected = selectedMemos.contains(memo.memoId),
                            isSelectionMode = isSelectionMode,
                            onClick = {
                                if (isSelectionMode) {
                                    memoViewModel.selectMemo(memo.memoId)
                                } else {
                                    navController.navigate(ScreenRoute.MemoDetail.createRoute(memo.memoId))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}