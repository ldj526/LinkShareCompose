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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.linksharecompose.nickname.NicknameViewModel

@Composable
fun SignupScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    nicknameViewModel: NicknameViewModel
) {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val signUpResult by authViewModel.signupResult.observeAsState()
    val isSignupLoading by authViewModel.isSignupLoading.observeAsState(initial = false)
    val isNicknameCheckLoading by nicknameViewModel.isNicknameCheckLoading.observeAsState(initial = false)

    val emailStatus by authViewModel.emailStatus.observeAsState()
    val isEmailAvailable by authViewModel.isEmailAvailable.observeAsState()
    val nicknameStatus by nicknameViewModel.nicknameStatus.observeAsState()
    val isNicknameAvailable by nicknameViewModel.isNicknameAvailable.observeAsState()
    val passwordStatus by authViewModel.passwordStatus.observeAsState()
    val confirmPasswordStatus by authViewModel.confirmPasswordStatus.observeAsState()

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
                    text = "회원가입",
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
                isError = nicknameStatus != null && nicknameStatus != "사용 가능한 닉네임입니다.",
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    authViewModel.onEmailChanged(it)
                },
                label = { Text("이메일") },
                singleLine = true,
                isError = emailStatus != null && emailStatus != "사용 가능한 이메일입니다.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                this@Column.AnimatedVisibility(visible = emailStatus != null) {
                    emailStatus?.let {
                        val textColor = when (it) {
                            "사용 가능한 이메일입니다." -> Color.Green
                            "이미 사용 중인 이메일입니다." -> Color.Red
                            "이메일 형식이 잘못됐습니다." -> Color.Red
                            "이메일 중복 확인에 실패했습니다." -> Color.Red
                            else -> Color.Black
                        }

                        Text(
                            text = it,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    authViewModel.onPasswordChanged(password, confirmPassword)
                },
                label = { Text("패스워드") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                singleLine = true,
                isError = passwordStatus != null && passwordStatus != "사용 가능한 비밀번호입니다.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                this@Column.AnimatedVisibility(visible = passwordStatus != null) {
                    passwordStatus?.let {
                        Text(
                            text = it,
                            color = if (it == "사용 가능한 비밀번호입니다.") Color.Green else Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    authViewModel.onPasswordChanged(password, confirmPassword)
                },
                label = { Text("패스워드 확인") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                singleLine = true,
                isError = confirmPasswordStatus != null && confirmPasswordStatus != "비밀번호가 일치합니다.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                this@Column.AnimatedVisibility(visible = confirmPasswordStatus != null) {
                    confirmPasswordStatus?.let {
                        Text(
                            text = it,
                            color = if (it == "비밀번호가 일치합니다.") Color.Green else Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                        authViewModel.signupUserWithEmail(email, password, nickname)
                },
                enabled = email.isNotEmpty() && nickname.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                        isEmailAvailable == true && isNicknameAvailable == true && !isSignupLoading &&
                        passwordStatus == "사용 가능한 비밀번호입니다." && confirmPasswordStatus == "비밀번호가 일치합니다.",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "회원가입")
                    if (isSignupLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }

        signUpResult?.let { result ->
            if (result.isSuccess) {
                LaunchedEffect(Unit) {
                    navController.navigate("loginScreen") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
                }
            } else if (result.isFailure) {
                val error = result.exceptionOrNull()?.message
                Toast.makeText(LocalContext.current, "회원가입 실패: $error", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}