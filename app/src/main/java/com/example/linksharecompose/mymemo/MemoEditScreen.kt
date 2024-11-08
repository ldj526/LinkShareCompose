package com.example.linksharecompose.mymemo

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MemoEditScreen(navController: NavController, memoViewModel: MemoViewModel) {
    val memo by memoViewModel.memo.observeAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }

    val isLoading by memoViewModel.isLoading.observeAsState(initial = false)
    val updateMemoResult by memoViewModel.updateMemoResult.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(memo) {
        memo?.let {
            title = it.title
            content = it.content
            photoUri = it.imageUri ?: ""
            linkUrl = it.linkUrl
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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

                Text(
                    text = "메모 수정",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, end = 45.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("제목") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = linkUrl,
            onValueChange = { linkUrl = it },
            label = { Text("링크") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // TODO : 사진 추가

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 200.dp),
            minLines = 6
        )

        Spacer(modifier = Modifier.height(8.dp))

        // TODO : 지도 추가

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    memo?.let {
                        val updatedMemo = it.copy(
                            title = title,
                            content = content,
                            imageUri = photoUri,
                            linkUrl = linkUrl,
                            timestamp = System.currentTimeMillis()
                        )
                        memoViewModel.updateMemo(updatedMemo)
                    }
                }, enabled = !isLoading, modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "수정")
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    navController.popBackStack()
                }, modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text("취소")
            }
        }
    }

    updateMemoResult?.let { result ->
        if (result.isSuccess) {
            memoViewModel.resetUpdateMemoResult()
            navController.popBackStack()
        } else {
            memoViewModel.resetUpdateMemoResult()
            val error = result.exceptionOrNull()?.message
            Toast.makeText(context, "메모 수정 실패: $error", Toast.LENGTH_LONG).show()
        }
    }
}