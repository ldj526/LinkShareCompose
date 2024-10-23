package com.example.linksharecompose

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SignupScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var nickname by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }
        var emailErrorMsg by remember { mutableStateOf<String?>(null) }
        var emailSuccessMsg by remember { mutableStateOf<String?>(null) }
        var nicknameErrorMsg by remember { mutableStateOf<String?>(null) }
        var nicknameSuccessMsg by remember { mutableStateOf<String?>(null) }
        var passwordErrorMsg by remember { mutableStateOf<String?>(null) }
        var confirmPasswordErrorMsg by remember { mutableStateOf<String?>(null) }
        var isNicknameAvailable by remember { mutableStateOf<Boolean?>(null) }
        var isEmailAvailable by remember { mutableStateOf<Boolean?>(null) }

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
                    nickname = it
                    nicknameErrorMsg = null
                    nicknameSuccessMsg = null
                    isNicknameAvailable = null
                },
                label = { Text("닉네임") },
                isError = nicknameErrorMsg != null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {

                },
                enabled = nickname.isNotEmpty(),
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(text = "중복체크")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ) {
            this@Column.AnimatedVisibility(visible = nicknameErrorMsg != null || nicknameSuccessMsg != null) {
                nicknameErrorMsg?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                } ?: nicknameSuccessMsg?.let {
                    Text(
                        text = it,
                        color = Color.Green,
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
                    emailErrorMsg = null
                    emailSuccessMsg = null
                    isEmailAvailable = null

                    if (it.isNotBlank()) {
                        emailErrorMsg =
                            if (android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                                null
                            } else {
                                "이메일 형식이 잘못됐습니다."
                            }
                        if (emailErrorMsg == null) {
                            /* 닉네임 중복 확인 로직 */
                        }
                    }
                },
                label = { Text("이메일") },
                singleLine = true,
                isError = emailErrorMsg != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                this@Column.AnimatedVisibility(visible = emailErrorMsg != null || emailSuccessMsg != null) {
                    emailErrorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    } ?: emailSuccessMsg?.let {
                        Text(
                            text = it,
                            color = Color.Green,
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
                    val pwdPattern =
                        "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]+$"
                    passwordErrorMsg = when {
                        it.isBlank() -> "비밀번호를 입력해주세요."
                        (it.length < 8 || it.length > 20) && it.matches(pwdPattern.toRegex()) -> "8자리 이상 20자리 이하로 입력해주세요"
                        (it.length < 8 || it.length > 20) && !it.matches(pwdPattern.toRegex()) -> "8자리 이상 20자리 이하로 입력해주세요\n영문, 숫자, 특수문자를 1개 이상 입력해주세요."
                        (it.length in 8..20) && !it.matches(pwdPattern.toRegex()) -> "영문, 숫자, 특수문자를 1개 이상 입력해주세요."
                        else -> null
                    }
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
                isError = passwordErrorMsg != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                this@Column.AnimatedVisibility(visible = passwordErrorMsg != null) {
                    passwordErrorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
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
                    confirmPasswordErrorMsg = when {
                        it.isBlank() -> "비밀번호를 입력해주세요."
                        it != password -> "비밀번호가 일치하지 않습니다."
                        else -> null
                    }
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
                isError = confirmPasswordErrorMsg != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                this@Column.AnimatedVisibility(visible = confirmPasswordErrorMsg != null) {
                    confirmPasswordErrorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (emailErrorMsg == null && nicknameErrorMsg == null && passwordErrorMsg == null &&
                        isEmailAvailable == true && isNicknameAvailable == true
                    ) {
                        /* 회원가입 로직 */
                    }
                },
                enabled = email.isNotEmpty() && nickname.isNotEmpty() && password.isNotEmpty() &&
                        emailErrorMsg == null && nicknameErrorMsg == null && passwordErrorMsg == null &&
                        isEmailAvailable == true && isNicknameAvailable == true,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text(text = "회원가입")
            }
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    SignupScreen(navController = navController)
}