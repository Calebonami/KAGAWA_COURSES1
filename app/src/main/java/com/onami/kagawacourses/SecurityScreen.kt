package com.onami.kagawacourses

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SecurityScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Security Settings",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SecurityOption(
            iconResId = R.drawable.baseline_security_24,
            title = "Two-Factor Authentication",
            onClick = {
                navController.navigate("twoFactorAuthentication")
            }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SecurityOption(
            iconResId = R.drawable.baseline_question_mark_24,
            title = "Set Security Questions",
            onClick = {
                navController.navigate("securityQuestions")
            }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SecurityOption(
            iconResId = R.drawable.baseline_history_24,
            title = "Account Activity Logs",
            onClick = {
                navController.navigate("accountActivityLogs")
            }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SecurityOption(
            iconResId = R.drawable.baseline_fingerprint_24,
            title = "Biometric Authentication",
            onClick = {
                navController.navigate("biometricAuthentication")
            }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SecurityOption(
            iconResId = R.drawable.baseline_devices_24,
            title = "Session Management",
            onClick = {
                navController.navigate("sessionManagement")
            }
        )

    }
}

@Composable
fun SecurityOption(iconResId: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
    }
}
