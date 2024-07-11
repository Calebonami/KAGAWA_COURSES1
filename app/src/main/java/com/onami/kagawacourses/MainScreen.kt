package com.onami.kagawacourses

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var courses by remember { mutableStateOf<List<Course>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loadCourses { fetchedCourses ->
            courses = fetchedCourses
        }
    }

    val bottomNavigationItems = listOf(
        BottomNavigationItem(icon = R.drawable.baseline_library_books_24, label = "Courses"),
        BottomNavigationItem(icon = R.drawable.baseline_settings_applications_24, label = "Settings"),
        BottomNavigationItem(icon = R.drawable.baseline_person_24, label = "Profile")
    )
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo4),
                            contentDescription = null,
                            modifier = Modifier
                                .size(180.dp)
                                .padding(start = 8.dp)
                        )
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Courses") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        IconButton(onClick = {
                            coroutineScope.launch {
                                performSearch(searchQuery, context) { fetchedCourses ->
                                    courses = fetchedCourses
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_find_replace_24),
                                contentDescription = "Search"
                            )
                        }
                    }
                },
                actions = {
                    // Empty as search is handled in the title Row
                }
            )
        },
        bottomBar = {
            CustomBottomNavigation(
                items = bottomNavigationItems,
                selectedItem = selectedItem,
                onItemSelected = {
                    selectedItem = it
                    when (it) {
                        0 -> navController.navigate("mainActivity")
                        1 -> navController.navigate("settingsScreen")
                        2 -> navController.navigate("profileScreen")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        checkIfUserHasPaidCourses { hasPaidCourses ->
                            if (hasPaidCourses) {
                                navController.navigate("myCoursesScreen")
                            } else {
                                Toast.makeText(context, "Oops! You have no courses in the list", Toast.LENGTH_SHORT).show()
                                navController.navigate("mainActivity")
                            }
                        }
                    }
                },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "My Courses")
            }

            Text(
                text = "Live Courses",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(courses) { course ->
                    CourseItem(course = course, navController = navController)
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun CourseItem(course: Course, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.graduate),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = course.title,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.clickable {
                    navController.navigate("courseDetails/${course.title}/${course.price}")
                }
            )
            Text(text = course.price, fontSize = 14.sp, color = Color.Red)
        }
    }
}

suspend fun loadCourses(onCoursesLoaded: (List<Course>) -> Unit) {
    val db = Firebase.firestore
    try {
        val querySnapshot = db.collection("courses").get().await()
        val fetchedCourses = querySnapshot.toObjects(Course::class.java)
        onCoursesLoaded(fetchedCourses)
        Log.d("MainScreen", "Courses fetched: $fetchedCourses")
    } catch (e: Exception) {
        Log.e("MainScreen", "Error getting courses", e)
    }
}

suspend fun performSearch(
    searchQuery: String,
    context: android.content.Context,
    onCoursesLoaded: (List<Course>) -> Unit
) {
    val db = Firebase.firestore
    try {
        val querySnapshot = db.collection("courses")
            .whereArrayContains("keywords", searchQuery.lowercase())
            .get()
            .await()
        val fetchedCourses = querySnapshot.toObjects(Course::class.java)
        if (fetchedCourses.isEmpty()) {
            Toast.makeText(context, "Course not found", Toast.LENGTH_SHORT).show()
        } else {
            onCoursesLoaded(fetchedCourses)
        }
    } catch (e: Exception) {
        Log.e("MainScreen", "Error searching for course", e)
        Toast.makeText(context, "Error searching for course", Toast.LENGTH_SHORT).show()
    }
}

suspend fun checkIfUserHasPaidCourses(onResult: (Boolean) -> Unit) {
    val db = Firebase.firestore
    try {
        val user = Firebase.auth.currentUser
        val querySnapshot = db.collection("courses")
            .whereEqualTo("userId", user?.uid)
            .whereEqualTo("paid", true)
            .get()
            .await()
        onResult(!querySnapshot.isEmpty)
    } catch (e: Exception) {
        Log.e("MainScreen", "Error checking for paid courses", e)
        onResult(false)
    }
}
