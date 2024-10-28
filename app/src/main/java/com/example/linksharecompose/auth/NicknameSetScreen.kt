package com.example.linksharecompose.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NicknameSetScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userId: String,
    onNicknameSetSuccess: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    val isNicknameCheckLoading by authViewModel.isNicknameCheckLoading.observeAsState(initial = false)
    val isNicknameAvailable by authViewModel.isNicknameAvailable.observeAsState()
    val nicknameStatus by authViewModel.nicknameStatus.observeAsState()
    val isSignupLoading by authViewModel.isSignupLoading.observeAsState(initial = false)
    val nicknameAddStatus by authViewModel.nicknameAddStatus.observeAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nickname,
                onValueChange = {
                    if (it.length <= 10) {
                        nickname = it
                        authViewModel.onNicknameChanged(it)
                    }
                },
                label = { Text("닉네임") },
                isError = nicknameStatus != null && nicknameStatus != "사용 가능한 닉네임입니다.",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    authViewModel.checkNicknameDuplication(nickname)
                },
                enabled = nickname.isNotEmpty() && !isNicknameCheckLoading &&
                        nicknameStatus != "닉네임은 한글, 영어, 숫자만 가능합니다.\n2자 이상 10자 이하로 입력해주세요.",
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "중복체크")
                    if (isNicknameCheckLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            this@Column.AnimatedVisibility(visible = nicknameStatus != null) {
                nicknameStatus?.let {
                    Text(
                        text = it,
                        color = if (it == "사용 가능한 닉네임입니다.") Color.Green else Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                authViewModel.addNickname(userId, nickname)
            },
            enabled = isNicknameAvailable == true,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "확인")
                if (isSignupLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        nicknameAddStatus?.let { result ->
            if (result.isSuccess) {
                onNicknameSetSuccess()
            } else if (result.isFailure) {
                val error = result.exceptionOrNull()?.message
                Toast.makeText(LocalContext.current, "닉네임 설정 실패: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
}