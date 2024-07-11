package com.onami.kagawacourses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    title: String?,
    price: String?,
    navController: NavHostController
) {
    val db = Firebase.firestore
    var course by remember { mutableStateOf<Course?>(null) }

    LaunchedEffect(Pair(title, price)) {
        try {
            val query = db.collection("courses")
                .whereEqualTo("title", title)
                .whereEqualTo("price", price)
                .limit(1)

            val snapshot = query.get().await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]
                course = document.toObject<Course>()
            }
        } catch (e: Exception) {
            // Handle error fetching course details
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                course?.let { course ->
                    Text(
                        text = course.title,
                        fontSize = 24.sp,
                        color = Color.Blue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = course.description,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Price: ${course.price}",
                        fontSize = 20.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate("payment/${course.title}/${course.price}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(text = "Pay Now")
                    }
                }
            }
        }
    }
}



