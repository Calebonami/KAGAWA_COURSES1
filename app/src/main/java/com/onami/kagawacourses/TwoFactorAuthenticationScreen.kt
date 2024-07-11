package com.onami.kagawacourses

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.*

@Composable
fun TwoFactorAuthenticationScreen(navController: NavController) {
    val context = LocalContext.current
    var isTwoFactorEnabled by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Title()
        StatusMessage(statusMessage)
        TwoFactorSwitch(
            isTwoFactorEnabled = isTwoFactorEnabled,
            isProcessing = isProcessing,
            onSwitchChange = { isChecked ->
                if (!isProcessing) {
                    isTwoFactorEnabled = isChecked
                    isProcessing = true
                    statusMessage = ""
                    scope.launch {
                        handleTwoFactorToggle(
                            context = context,
                            isEnabled = isChecked,
                            onSuccess = {
                                isProcessing = false
                                Toast.makeText(context, if (isChecked) "2FA Enabled" else "2FA Disabled", Toast.LENGTH_SHORT).show()
                            },
                            onError = { error ->
                                isProcessing = false
                                statusMessage = error ?: "An unexpected error occurred"
                                isTwoFactorEnabled = !isChecked
                            }
                        )
                    }
                }
            }
        )
        ProcessingIndicator(isProcessing)
        TwoFactorStatus(isTwoFactorEnabled)
    }
}

@Composable
fun Title() {
    Text(
        text = "Two-Factor Authentication",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun StatusMessage(statusMessage: String) {
    if (statusMessage.isNotEmpty()) {
        Text(
            text = statusMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun TwoFactorSwitch(
    isTwoFactorEnabled: Boolean,
    isProcessing: Boolean,
    onSwitchChange: (Boolean) -> Unit
) {
    Switch(
        checked = isTwoFactorEnabled,
        onCheckedChange = { isChecked -> onSwitchChange(isChecked) },
        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
        enabled = !isProcessing
    )
}

@Composable
fun ProcessingIndicator(isProcessing: Boolean) {
    if (isProcessing) {
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator()
    }
}

@Composable
fun TwoFactorStatus(isTwoFactorEnabled: Boolean) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = if (isTwoFactorEnabled) "2FA is enabled." else "2FA is disabled.")
}

suspend fun handleTwoFactorToggle(
    context: Context,
    isEnabled: Boolean,
    onSuccess: () -> Unit,
    onError: (String?) -> Unit
) {
    try {
        val result = if (isEnabled) enableTwoFactorAuthentication(context) else disableTwoFactorAuthentication(context)
        if (result.success) {
            onSuccess()
        } else {
            onError(result.error)
        }
    } catch (e: Exception) {
        onError(e.message)
    }
}

data class Result(val success: Boolean, val error: String?)

fun enableTwoFactorAuthentication(context: Context): Result {
    return try {
        // Simulate network call
        Thread.sleep(1000)
        Result(true, null)
    } catch (e: Exception) {
        Result(false, e.message)
    }
}

fun disableTwoFactorAuthentication(context: Context): Result {
    return try {
        // Simulate network call
        Thread.sleep(1000)
        Result(true, null)
    } catch (e: Exception) {
        Result(false, e.message)
    }
}
