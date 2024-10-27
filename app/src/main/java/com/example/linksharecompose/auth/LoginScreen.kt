package com.example.linksharecompose.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavHostController
import com.example.linksharecompose.BuildConfig
import com.example.linksharecompose.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToNicknameSet: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoginLoading by authViewModel.isLoginLoading.observeAsState(initial = false)
    val emailLoginResult by authViewModel.emailLoginResult.observeAsState()
    val googleLoginResult by authViewModel.googleLoginResult.observeAsState()
    val context = LocalContext.current

    val credentialManager = remember { CredentialManager.create(context) }  // 인스턴스 생성
    val coroutineScope = rememberCoroutineScope()   // Compose와 함께 사용하는 CoroutineScope
    // Google ID 토큰 요청
    val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_CLIENT_ID)
        .setNonce("<nonce string to use when generating a Google ID token>")
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로그인",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 64.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 128.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "회원가입",
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate("signupScreen")
                    }
                    .padding(16.dp),
                textAlign = TextAlign.Center)

            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color.Gray)
            )

            Text(
                text = "아이디/비밀번호 찾기",
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate("")
                    }
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                authViewModel.signInWithEmailAndPassword(email, password)
            },
            enabled = email.isNotEmpty() && password.isNotEmpty() && !isLoginLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "로그인")
                if (isLoginLoading) {
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
                handleGoogleSignIn(context, signInWithGoogleOption, credentialManager, coroutineScope, authViewModel)
            },
            enabled = !isLoginLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_button_logo),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "구글로 로그인", color = Color.Black)
                }

                if (isLoginLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }

    // 구글 로그인 상태 확인 후 처리
    googleLoginResult?.let { result ->
        if (result.isSuccess) {
            val user = result.getOrNull()
            if (user != null) {
                authViewModel.checkAndNavigateToNicknameScreen(user, onNavigateToNicknameSet= { userId ->
                    navController.navigate("nicknameSetScreen/$userId")
                }, onLoginSuccess = {
                    onLoginSuccess()
                })
            }
        } else if (result.isFailure) {
            val error = result.exceptionOrNull()?.message
            Toast.makeText(context, "구글 로그인 실패: $error", Toast.LENGTH_LONG).show()
        }
    }

    // 이메일 로그인 상태 확인 후 처리
    emailLoginResult?.let { result ->
        if (result.isSuccess) {
            onLoginSuccess()
        } else if (result.isFailure) {
            val error = result.exceptionOrNull()?.message
            Toast.makeText(LocalContext.current, "로그인 실패: $error", Toast.LENGTH_LONG)
                .show()
        }
    }
}

// 구글 로그인 처리
fun handleGoogleSignIn(
    context: Context,
    signInWithGoogleOption: GetSignInWithGoogleOption,
    credentialManager: CredentialManager,
    coroutineScope: CoroutineScope,
    authViewModel: AuthViewModel,
) {
    // 구글 로그인 자격 증명 요청
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    // 비동기처리
    coroutineScope.launch {
        try {
            val credentialResult = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = credentialResult.credential

            when (credential) {
                is GoogleIdTokenCredential -> {
                    // Google ID 토큰으로 로그인 시도
                    val idToken = credential.idToken
                    authViewModel.signInWithGoogle(idToken)
                }
                is CustomCredential -> {
                    // CustomCredential 유형인 경우 GoogleIdTokenCredential로 변환 후 로그인 시도
                    try {
                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleCredential.idToken
                        authViewModel.signInWithGoogle(idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Toast.makeText(context, "Google ID 토큰 파싱 실패: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Log.e("LoginScreen", "Unexpected type of credential: ${credential.javaClass.name}")
                }
            }
        } catch (e: GetCredentialException) {
            Toast.makeText(context, "Google Sign-In 오류: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}