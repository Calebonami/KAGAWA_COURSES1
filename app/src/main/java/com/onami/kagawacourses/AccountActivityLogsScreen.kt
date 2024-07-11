package com.onami.kagawacourses

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ActivityLog(
    val activity: String,
    val timestamp: String // Use appropriate data type as per your backend or data source
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountActivityLogsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AccountActivityLogsViewModel = viewModel()
    val activityLogs by viewModel.activityLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Activity Logs") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (activityLogs.isNotEmpty()) {
                    ActivityLogsList(activityLogs = activityLogs)
                } else {
                    Text("No activity logs available.")
                }
            }
        }
    )
}

@Composable
private fun ActivityLogsList(activityLogs: List<ActivityLog>) {
    LazyColumn {
        items(activityLogs) { log ->
            ActivityLogItem(log = log)
        }
    }
}

@Composable
private fun ActivityLogItem(log: ActivityLog) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Activity: ${log.activity}",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Timestamp: ${log.timestamp}",
            fontSize = 14.sp,
        )
        // Add more details as needed from the activity log
    }
}

class AccountActivityLogsViewModel : ViewModel() {
    // Mock data for demonstration
    private val _activityLogs: MutableStateFlow<List<ActivityLog>> = MutableStateFlow(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs

    init {
        // Simulate fetching activity logs from a repository or API
        fetchActivityLogs()
    }

    private fun fetchActivityLogs() {
        // Replace with actual logic to fetch activity logs from your data source
        val logs = listOf(
            ActivityLog("Logged in", "2024-06-17 10:30 AM"),
            ActivityLog("Changed password", "2024-06-16 02:15 PM"),
            ActivityLog("Updated profile", "2024-06-15 11:45 AM")
            // Add more logs as needed
        )
        _activityLogs.value = logs
    }
}
