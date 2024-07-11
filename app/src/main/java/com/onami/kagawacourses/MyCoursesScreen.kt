package com.onami.kagawacourses

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class CoursesState(
    val loading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val error: String? = null
)

class CoursesViewModel : ViewModel() {
    private val _courses = MutableStateFlow(CoursesState())
    val courses: StateFlow<CoursesState> get() = _courses

    init {
        fetchCourses()
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            try {
                val courses = fetchCoursesFromFirestore()
                _courses.value = CoursesState(loading = false, courses = courses)
            } catch (e: Exception) {
                _courses.value = CoursesState(loading = false, error = e.message)
            }
        }
    }

    val recommendedCourses: List<Course> = listOf(
        // Mock data for recommended courses
        Course(id = "1", title = "Recommended Course 1"),
        Course(id = "2", title = "Recommended Course 2")
    )
}

suspend fun fetchCoursesFromFirestore(): List<Course> {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser ?: return emptyList()
    val courses = mutableListOf<Course>()

    try {
        val snapshot = db.collection("courses")
            .whereEqualTo("userId", user.uid)
            .get()
            .await()
        for (document in snapshot.documents) {
            val course = document.toObject(Course::class.java)
            course?.let { courses.add(it) }
        }
    } catch (e: Exception) {
        throw e
    }
    return courses
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesScreen(navController: NavController, viewModel: CoursesViewModel = viewModel()) {
    val coursesState by viewModel.courses.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Courses") }) },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when {
                    coursesState.loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    coursesState.error != null -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Error: ${coursesState.error}", color = Color.Red)
                            }
                        }
                    }
                    coursesState.courses.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No courses available")
                            }
                        }
                    }
                    else -> {
                        item { CourseSection(navController, "Ongoing Courses", coursesState.courses.filter { !it.completed && !it.archived }) }
                        item { Divider() }
                        item { CourseSection(navController, "Completed Courses", coursesState.courses.filter { it.completed }) }
                        item { Divider() }
                        item { CourseSection(navController, "Favorite Courses", coursesState.courses.filter { it.favorite }) }
                        item { Divider() }
                        item { CourseSection(navController, "Recommended Courses", viewModel.recommendedCourses) }
                        item { Divider() }
                        item { CourseSection(navController, "Archived Courses", coursesState.courses.filter { it.archived }) }
                        item { Divider() }
                        item { AdditionalFeaturesSection(navController) }
                    }
                }
            }
        }
    )
}

@Composable
fun CourseSection(navController: NavController, title: String, courses: List<Course>) {
    Section(title = title) {
        courses.forEach { course ->
            CourseItem(
                iconResId = R.drawable.baseline_golf_course_24,
                title = course.title,
                onClick = { /* Navigate to course details */ }
            )
        }
    }
}

@Composable
fun AdditionalFeaturesSection(navController: NavController) {
    Section(title = "Additional Features") {
        FeatureItem(iconResId = R.drawable.baseline_card_giftcard_24, title = "Certificates", onClick = { /* Handle Click */ })
        FeatureItem(iconResId = R.drawable.baseline_download_24, title = "Downloads", onClick = { /* Handle Click */ })
        FeatureItem(iconResId = R.drawable.baseline_forum_24, title = "Discussion Forums", onClick = { /* Handle Click */ })
        FeatureItem(iconResId = R.drawable.baseline_calendar_month_24, title = "Calendar", onClick = { /* Handle Click */ })
        FeatureItem(iconResId = R.drawable.baseline_settings_applications_24, title = "Course Settings", onClick = { /* Handle Click */ })
    }
}

@Composable
fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun CourseItem(iconResId: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
    }
}

@Composable
fun FeatureItem(iconResId: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp)
    }
}
