package com.onami.kagawacourses

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
        content = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item { AccountSettingsSection(navController) }
                item { Divider() }
                item { PrivacySettingsSection(navController) }
                item { Divider() }
                item { NotificationSettingsSection(navController) }
                item { Divider() }
                item { GeneralSettingsSection(navController) }
                item { Divider() }
                item { LogoutSection(navController) }
            }
        }
    )
}

@Composable
fun AccountSettingsSection(navController: NavController) {
    SettingsSection(title = "Account Settings") {
        SettingsItem(iconResId = R.drawable.baseline_manage_accounts_24, title = "Edit Profile", onClick = { navController.navigate("editProfile") })
        SettingsItem(iconResId = R.drawable.baseline_email_24, title = "Change Email", onClick = { navController.navigate("changeEmail") })
        SettingsItem(iconResId = R.drawable.baseline_password_24, title = "Change Password", onClick = { navController.navigate("changePassword") })
    }
}

@Composable
fun PrivacySettingsSection(navController: NavController) {
    SettingsSection(title = "Privacy Settings") {
        SettingsItem(iconResId = R.drawable.baseline_private_connectivity_24, title = "Privacy Policy", onClick = { navController.navigate("privacyPolicy") })
        SettingsItem(iconResId = R.drawable.baseline_security_24, title = "Security", onClick = { navController.navigate("securityScreen") })
    }
}

@Composable
fun NotificationSettingsSection(navController: NavController) {
    SettingsSection(title = "Notification Settings") {
        SettingsItem(iconResId = R.drawable.baseline_notifications_active_24, title = "Email Notifications", onClick = { navController.navigate("emailNotifications") })
        SettingsItem(iconResId = R.drawable.onami, title = "Push Notifications", onClick = { navController.navigate("pushNotifications") })
        SettingsItem(iconResId = R.drawable.baseline_surround_sound_24, title = "Sound & Vibration", onClick = { navController.navigate("soundAndVibration") })
    }
}

@Composable
fun GeneralSettingsSection(navController: NavController) {
    val context = LocalContext.current

    SettingsSection(title = "General Settings") {
        SettingsItem(
            iconResId = R.drawable.baseline_help_center_24,
            title = "Help & Feedback",
            onClick = {
                val intent = Intent(context, FeedbackFormActivity::class.java)
                context.startActivity(intent)
            }
        )
    }
}


@Composable
fun LogoutSection(navController: NavController) {
    SettingsSection(title = "Logout") {
        SettingsItem(iconResId = R.drawable.baseline_logout_24, title = "Logout", onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("signUpScreen") { launchSingleTop = true }
        })
    }
}

// Logout Logic
fun logout(navController: NavController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate("SignUpScreen")
}


@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun SettingsItem(iconResId: Int, title: String, onClick: () -> Unit) {
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
fun EditProfileScreen(navController: NavController) {
    val userName = remember { mutableStateOf(TextFieldValue()) }
    val phoneNumber = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = userName.value,
            onValueChange = { userName.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                editProfile(userName.value.text, phoneNumber.value.text)
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save")
        }
    }
}

// Edit Profile Logic
fun editProfile(userName: String, phoneNumber: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    userId?.let { uid ->
        val userData = mapOf(
            "username" to userName,
            "phoneNumber" to phoneNumber
        )

        usersRef.child(uid).updateChildren(userData)
            .addOnSuccessListener {
                // Update successful
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }
}

@Composable
fun ChangeEmailScreen(navController: NavController) {
    val newEmail = remember { mutableStateOf(TextFieldValue()) }
    val reason = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = newEmail.value,
            onValueChange = { newEmail.value = it },
            label = { Text("New Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = reason.value,
            onValueChange = { reason.value = it },
            label = { Text("Reason for Change") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                requestEmailChange(newEmail.value.text, reason.value.text)
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Submit Request")
        }
    }
}

// Request Email Change Logic
fun requestEmailChange(newEmail: String, reason: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val emailChangeRequestsRef = FirebaseDatabase.getInstance().getReference("emailChangeRequests")

    userId?.let { uid ->
        val requestData = mapOf(
            "userId" to uid,
            "newEmail" to newEmail,
            "reason" to reason,
            "status" to "pending"
        )

        val requestId = emailChangeRequestsRef.push().key
        requestId?.let {
            emailChangeRequestsRef.child(it).setValue(requestData)
                .addOnSuccessListener {
                    // Request submitted successfully
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }
}

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val oldPassword = remember { mutableStateOf(TextFieldValue()) }
    val newPassword = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = oldPassword.value,
            onValueChange = { oldPassword.value = it },
            label = { Text("Old Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                changePassword(oldPassword.value.text, newPassword.value.text)
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Change Password")
        }
    }
}

// Change Password Logic
fun changePassword(oldPassword: String, newPassword: String) {
    val user = FirebaseAuth.getInstance().currentUser
    val credential = EmailAuthProvider.getCredential(user?.email ?: "", oldPassword)

    user?.reauthenticate(credential)?.addOnCompleteListener { reAuthTask ->
        if (reAuthTask.isSuccessful) {
            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    // Password updated successfully
                } else {
                    // Handle update failure
                }
            }
        } else {
            // Handle reauthentication failure
        }
    }
}

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Privacy Policy",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = """
 At Kagawa Developers, we value your privacy and are committed to protecting your personal information. This Privacy Policy outlines the types of information we collect from you when you use our online courses and services, how we use and protect that information, and your rights regarding your personal data. When you register for an account, we collect personal information such as your name, email address, and payment information. We use this information to provide you with access to our courses, process transactions, and communicate with you about your account and our services. Additionally, we may collect information about your usage of our courses, including your progress, preferences, and any feedback you provide. This information helps us improve our offerings and provide you with a better learning experience.

We use cookies and similar technologies to enhance your experience on our platform. These technologies allow us to recognize you when you return, remember your preferences, and analyze how you interact with our services. Cookies are small data files stored on your device that help us keep track of your activity and compile usage data, which we use to improve our site and services. You can manage your cookie preferences through your browser settings, and most browsers allow you to block cookies, although doing so may affect your ability to use some features of our platform.

We take the security of your personal information seriously and implement appropriate technical and organizational measures to protect it from unauthorized access, disclosure, alteration, or destruction. Our security practices include encryption, firewalls, secure servers, and regular security assessments. However, please be aware that no method of transmission over the internet or electronic storage is completely secure, and we cannot guarantee absolute security. We encourage you to take steps to protect your information by using strong passwords and keeping your login credentials confidential.

We may share your information with third-party service providers who assist us in operating our platform, processing payments, or delivering communications. These providers are contractually obligated to keep your information confidential and use it only for the purposes we specify. We ensure that any third party with whom we share your data adheres to privacy standards that are at least as stringent as those outlined in this policy. Additionally, we may disclose your information if required by law or in response to a legal request, such as a court order or subpoena.

You have the right to access, correct, or delete your personal information. You can do this by logging into your account and navigating to the account settings section, or by contacting our support team. Additionally, you can opt out of receiving promotional communications from us at any time by following the unsubscribe instructions included in those communications. Please note that even if you opt out of receiving promotional messages, we may still send you administrative messages regarding your account or transactions.

Our services are not intended for children under the age of 13, and we do not knowingly collect personal information from children. If we become aware that we have inadvertently received personal information from a child under 13, we will delete such information from our records. We encourage parents and guardians to monitor their children’s online activities and to help enforce our privacy policy by instructing their children never to provide personal information on our platform without their permission.

We may update this Privacy Policy from time to time to reflect changes in our practices, technology, legal requirements, or other factors. We will notify you of any significant changes by posting the new policy on our platform and updating the effective date. We encourage you to review this policy periodically to stay informed about how we are protecting your information. Your continued use of our services after any changes to this policy constitutes your acceptance of the updated terms.

If you have any questions or concerns about our Privacy Policy or our data practices, please contact us at info@kagawa1.dev. We are committed to resolving any issues you may have and to safeguarding your privacy. Your trust is important to us, and we strive to provide a safe and secure environment for your learning experience. Thank you for trusting [Your Company Name] with your personal information and for using our services.
            """.trimIndent(),
            fontSize = 16.sp,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back")
        }
    }
}


