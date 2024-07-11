package com.onami.kagawacourses

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricAuthenticationScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: BiometricAuthenticationViewModel = viewModel()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biometric Authentication") },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enable Biometric Authentication",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                BiometricSwitch(
                    isChecked = isBiometricEnabled,
                    onCheckedChange = { isChecked ->
                        viewModel.setBiometricEnabled(isChecked)
                        if (isChecked) {
                            // Initiate biometric authentication setup
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                showBiometricPrompt(context)
                            } else {
                                Toast.makeText(context, "Biometric authentication not supported on this device", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun BiometricSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Green, // Customizing checked thumb color
            uncheckedThumbColor = Color.Gray // Customizing unchecked thumb color
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

class BiometricAuthenticationViewModel : ViewModel() {
    private val _isBiometricEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled

    fun setBiometricEnabled(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
    }
}

@RequiresApi(Build.VERSION_CODES.P)
private fun showBiometricPrompt(context: Context) {
    // Replace with your own implementation of BiometricPrompt
    // This is a placeholder for demonstration
    Toast.makeText(context, "Biometric Prompt Shown", Toast.LENGTH_SHORT).show()
}
