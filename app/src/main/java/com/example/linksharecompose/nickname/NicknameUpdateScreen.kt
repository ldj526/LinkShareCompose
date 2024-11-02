package com.example.linksharecompose.nickname

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.linksharecompose.R
import com.example.linksharecompose.utils.ScreenRoute
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NicknameUpdateScreen(navController: NavController, nicknameViewModel: NicknameViewModel) {

    var nickname by remember { mutableStateOf("") }
    val isNicknameCheckLoading by nicknameViewModel.isNicknameCheckLoading.observeAsState(initial = false)
    val isNicknameAvailable by nicknameViewModel.isNicknameAvailable.observeAsState()
    val nicknameStatus by nicknameViewModel.nicknameStatus.observeAsState()
    val isSignupLoading by nicknameViewModel.setNicknameLoading.observeAsState(initial = false)
    val nicknameSetStatus by nicknameViewModel.nicknameSetStatus.observeAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        nicknameViewModel.resetNicknameCheckStatus()
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
                    text = "닉네임 변경",
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

        Spacer(modifier = Modifier.height(16.dp))

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
                        nicknameViewModel.onNicknameChanged(it)
                    }
                },
                label = { Text("닉네임") },
                isError = nicknameStatus != null && nicknameStatus != stringResource(R.string.enable_nickname),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    nicknameViewModel.checkNicknameDuplication(nickname)
                },
                enabled = nickname.isNotEmpty() && !isNicknameCheckLoading &&
                        nicknameStatus != stringResource(R.string.nickname_error_msg),
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
                        color = if (it == stringResource(R.string.enable_nickname)) Color.Green else Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                nicknameViewModel.setNickname(userId!!, nickname)
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

        nicknameSetStatus?.let { result ->
            if (result.isSuccess) {
                nicknameViewModel.resetNicknameUpdateResult()
                navController.navigate(ScreenRoute.Settings.route) {
                    popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                        inclusive = true
                    }
                }
            } else if (result.isFailure) {
                val error = result.exceptionOrNull()?.message
                Toast.makeText(LocalContext.current, "닉네임 설정 실패: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
}