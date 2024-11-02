package com.example.linksharecompose.settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.linksharecompose.R
import com.example.linksharecompose.utils.CustomDialog
import com.example.linksharecompose.utils.ScreenRoute
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    auth: FirebaseAuth,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val currentUser = auth.currentUser
    val userEmail = currentUser?.email
    val authProvider = currentUser?.providerData?.let { providers ->
        when {
            providers.map { it.providerId }.contains("google.com") -> "구글"
            else -> "이메일"
        }
    }

    val deleteAccountStatus by settingsViewModel.deleteAccountStatus.observeAsState()
    val isLoading by settingsViewModel.isLoading.observeAsState(initial = false)
    val getNicknameStatus by settingsViewModel.getNicknameStatus.observeAsState()
    val isNicknameLoading by settingsViewModel.isNicknameLoading.observeAsState(initial = false)

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            settingsViewModel.getNickname(userId)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp).fillMaxWidth().fillMaxHeight().aspectRatio(1f))
        } else {
            Text(
                text = stringResource(R.string.user_info),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = authProvider ?: stringResource(R.string.no_info),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = userEmail ?: stringResource(R.string.no_info),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(R.string.nickname),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(ScreenRoute.NicknameUpdate.route)
                }
                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {

                if (isNicknameLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(start = 8.dp).size(24.dp)
                    )
                } else {
                    val nicknameText = when {
                        getNicknameStatus?.isSuccess == true -> {
                            getNicknameStatus?.getOrNull() ?: stringResource(R.string.no_info)
                        }
                        else -> { stringResource(R.string.fail_to_load_nickname) }
                    }
                    Text(
                        text = nicknameText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.navigate_to_nickname_update),
                    tint = Color.Gray,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(R.string.app_info),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = stringResource(R.string.logout),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            Text(
                text = stringResource(R.string.withdraw),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWithdrawDialog = true }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    deleteAccountStatus?.let { result ->
        if (result.isSuccess) {
            onLogout()
            settingsViewModel.resetDeleteAccountStatus()
        } else {
            val error = result.exceptionOrNull()?.message
            Toast.makeText(LocalContext.current, "회원탈퇴 실패: $error", Toast.LENGTH_LONG)
                .show()
            Log.d("error", "$error")
            settingsViewModel.resetDeleteAccountStatus()
        }
    }

    // 로그아웃 시 Dialog
    if (showLogoutDialog) {
        CustomDialog(
            title = stringResource(R.string.logout_message),
            onConfirm = {
                onLogout()
                showLogoutDialog = false
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    // 회원 탈퇴 시 Dialog
    if (showWithdrawDialog) {
        CustomDialog(
            title = stringResource(R.string.withdraw_title_message),
            message = stringResource(R.string.withdraw_message),
            onConfirm = {
                settingsViewModel.deleteAccount(password)
                showWithdrawDialog = false
            },
            onDismiss = {
                showWithdrawDialog = false
            },
            messageColor = Color.Red
        ) {
            Column {
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
            }
        }
    }
}