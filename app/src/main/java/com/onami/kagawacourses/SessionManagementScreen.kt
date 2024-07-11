package com.onami.kagawacourses

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DeviceSession(
    val deviceId: String,
    val deviceName: String,
    val lastActive: Long,
    val isActive: Boolean
)

class SessionManagementViewModel : ViewModel() {

    private val _deviceSessions: MutableStateFlow<List<DeviceSession>> = MutableStateFlow(
        listOf(
            DeviceSession("device1", "Device 1", System.currentTimeMillis(), true),
            DeviceSession("device2", "Device 2", System.currentTimeMillis() - 1000 * 60 * 60 * 24, false),
            DeviceSession("device3", "Device 3", System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2, false)
        )
    )

    val deviceSessions: StateFlow<List<DeviceSession>> = _deviceSessions

    fun logout(deviceId: String) {
        _deviceSessions.value = _deviceSessions.value.map {
            if (it.deviceId == deviceId) {
                it.copy(isActive = false)
            } else {
                it
            }
        }
    }

    fun removeAllSessions() {
        _deviceSessions.value = emptyList()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionManagementScreen(navController: NavController) {
    val viewModel: SessionManagementViewModel = viewModel()
    val deviceSessions by viewModel.deviceSessions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Device Session Management",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.removeAllSessions() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove All Sessions")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn {
                    items(deviceSessions) { session ->
                        DeviceSessionItem(session = session) {
                            viewModel.logout(session.deviceId)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun DeviceSessionItem(session: DeviceSession, onLogoutClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Device: ${session.deviceName}", fontWeight = FontWeight.Bold)
                Text(text = "Last Active: ${session.lastActive}")
                Text(text = if (session.isActive) "Active" else "Inactive")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onLogoutClick) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        }
    }
}
