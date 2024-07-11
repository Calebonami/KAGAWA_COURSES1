package com.onami.kagawacourses

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var user by remember { mutableStateOf(User()) }

    LaunchedEffect(Unit) {
        fetchUserDetails { fetchedUser ->
            user = fetchedUser
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("User Profile") }) },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    item { ProfilePictureSection(user = user) }
                    item { ProfileDetailsSection(user = user) }
                    item { UserBioSection(user = user) }
                    item { UserSocialLinksSection() }
                    item { UserAchievementsSection() }
                    item { UserPreferencesSection() }
                }
            }
        }
    )
}


@Composable
fun ProfilePictureSection(user: User) {
    UserSection(title = "User Picture") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (user.profileImageUrl.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(data = user.profileImageUrl),
                    contentDescription = stringResource(id = R.string.person_image_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_pin_24),
                    contentDescription = stringResource(id = R.string.person_image_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}


@Composable
fun ProfileDetailsSection(user: User) {
    var user by remember { mutableStateOf(User()) }

    LaunchedEffect(Unit) {
        fetchUserDetails { fetchedUser ->
            user = fetchedUser
        }
    }

    UserSection(title = "Personal Details") {
        UserItem(
            iconResId = R.drawable.baseline_drive_file_rename_outline_24,
            title = user.username
        )
        UserItem(
            iconResId = R.drawable.baseline_email_24,
            title = user.email
        )
        UserItem(
            iconResId = R.drawable.baseline_settings_phone_24,
            title = user.phoneNumber
        )
        UserItem(
            iconResId = R.drawable.baseline_maps_home_work_24,
            title = user.address
        )
    }
}

@Composable
fun UserBioSection(user: User) {
    UserSection(title = "User Bio") {
        UserItem(
            iconResId = R.drawable.baseline_biotech_24,
            title = user.bio
        )
    }
}


@Composable
fun UserSocialLinksSection() {
    UserSection(title = "Social Links") {
        UserItem(
            iconResId = R.drawable.facebook,
            title = "Facebook"
        )
        UserItem(
            iconResId = R.drawable.twiter,
            title = "Twitter"
        )
        UserItem(
            iconResId = R.drawable.linkedin,
            title = "LinkedIn"
        )
    }
}

@Composable
fun UserAchievementsSection() {
    UserSection(title = "Achievements") {
        UserItem(
            iconResId = R.drawable.baseline_card_giftcard_24,
            title = "5 Completed Courses"
        )
    }
}

@Composable
fun UserPreferencesSection() {
    UserSection(title = "Preferences") {
        UserItem(
            iconResId = R.drawable.baseline_settings_applications_24,
            title = "Language: English"
        )
    }
}

@Composable
fun UserSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun UserItem(iconResId: Int, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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


fun fetchUserDetails(onUserFetched: (User) -> Unit) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    val userId = auth.currentUser?.uid ?: return

    database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val user = dataSnapshot.getValue(User::class.java)
            if (user != null) {
                onUserFetched(user)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle possible errors
        }
    })
}
