package com.onami.kagawacourses

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun FrontPage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Header()
        HeroSection(navController)
        KeyFeaturesSection()
        PopularCoursesSection(navController)
        InteractiveCommunitySection(navController)
        Footer()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kagawa Courses",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                )
            }
        }
    )
}

@Composable
fun HeroSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Learn Modern Technology",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp, color = Color.Black),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "With the best courses and interactive environment",
            style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = { navController.navigate("loginScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Get Started", color = Color.White)
        }
    }
}

@Composable
fun KeyFeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Our Key Features",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            KeyFeatureItem("Expert Instructors", R.drawable.expert)
            KeyFeatureItem("Interactive Courses", R.drawable.interactive)
            KeyFeatureItem("Flexible Learning", R.drawable.onetwo)
        }
    }
}

@Composable
fun KeyFeatureItem(feature: String, iconId: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = feature,
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = feature,
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black)
        )
    }
}

@Composable
fun PopularCoursesSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Popular Courses",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        val courses = listOf(
            "Full Stack Web Development",
            "Data Science and Machine Learning",
            "Cybersecurity"
        )
        Column {
            courses.forEach { course ->
                Text(
                    text = course,
                    style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { navController.navigate("courseDetail/$course") }
                )
            }
        }
    }
}


@Composable
fun InteractiveCommunitySection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Join Our Community",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Engage with fellow learners and instructors.",
            style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = { navController.navigate("signUpScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Join Now", color = Color.White)
        }
    }
}

@Composable
fun Footer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(16.dp)
    ) {
        Text(
            text = "Contact Us",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Email: support@kagawa1.dev",
            style = TextStyle(fontSize = 16.sp, color = Color.White),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Phone: +254747597182",
            style = TextStyle(fontSize = 16.sp, color = Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialMediaIcon(R.drawable.facebook, "Facebook")
            SocialMediaIcon(R.drawable.twiter, "Twitter")
            SocialMediaIcon(R.drawable.linkedin, "LinkedIn")
        }
    }
}

@Composable
fun SocialMediaIcon(iconId: Int, contentDescription: String) {
    Icon(
        painter = painterResource(id = iconId),
        contentDescription = contentDescription,
        tint = Color.White,
        modifier = Modifier.size(40.dp)
    )
}
