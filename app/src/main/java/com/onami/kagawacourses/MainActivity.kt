package com.onami.kagawacourses

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KagawaCoursesApp(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        setContent {
            KagawaCoursesApp(intent)
        }
    }
}

@Composable
fun KagawaCoursesApp(intent: Intent?) {
    val navController = rememberNavController()
    MainContent(navController = navController, intent = intent)
}

@Composable
fun MainContent(navController: NavHostController, intent: Intent?) {
    if (intent != null) {
        HandleDeepLink(navController, intent)
    }
    NavGraph(navController = navController)
}

@Composable
fun HandleDeepLink(navController: NavHostController, intent: Intent) {
    if (intent.action == Intent.ACTION_VIEW) {
        val uri = intent.data
        uri?.let {
            if (it.toString() == "android-app://androidx.navigation/mainActivity") {
                navController.navigate("mainScreen")
            }
        }
    }
}


