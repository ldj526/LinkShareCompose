package com.example.linksharecompose.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.linksharecompose.R
import com.example.linksharecompose.utils.CustomDialog

@Composable
fun SettingsScreen(navController: NavHostController, onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.user_info),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "로그인 플랫폼",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, bottom = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "이메일",
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
            text = stringResource(R.string.change_nickname),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("changeNickname") }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(R.string.app_info),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("appInfo") }
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
                .clickable { }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }

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
}