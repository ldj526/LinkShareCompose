package com.example.linksharecompose.mymemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.linksharecompose.R
import com.example.linksharecompose.utils.CustomDialog
import com.example.linksharecompose.utils.ScreenRoute

@Composable
fun MemoDetailScreen(navController: NavController, memoViewModel: MemoViewModel, memoId: String) {

    LaunchedEffect(memoId) {
        memoViewModel.fetchMemoById(memoId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val memo by memoViewModel.memo.observeAsState()
    val isLoading by memoViewModel.isLoading.observeAsState(initial = false)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(45.dp)
                        .padding(start = 15.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = memo?.title ?: "",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        memo?.let {
                            showDeleteDialog = true
                        }
                    },
                    modifier = Modifier
                        .size(45.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제"
                    )
                }

                IconButton(
                    onClick = {
                        navController.navigate(ScreenRoute.MemoEdit.route)
                    },
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "수정"
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )
            } else {
                memo?.let {
                    Text(
                        text = "Link URL",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = it.linkUrl,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "내용",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = it.content,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }

    // 메모 삭제 시 Dialog
    if (showDeleteDialog) {
        CustomDialog(
            title = stringResource(R.string.delete_memo),
            onConfirm = {
                memoViewModel.deleteMemoById(memoId)
                showDeleteDialog = false
                navController.popBackStack()
            },
            onDismiss = {
                showDeleteDialog = false
            },
        )
    }
}