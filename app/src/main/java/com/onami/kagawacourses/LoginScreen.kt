package com.onami.kagawacourses

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Sign In") }
        )
        Text(
            text = "Attention!!! First time login? Reset password to continue.",
            color = Color.Red,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    if (user != null) {
                                        checkProfileCompletion(user, navController)
                                    } else {
                                        errorMessage = "User not found"
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Sign In Failed"
                                }
                            }
                    } else {
                        errorMessage = "Please enter valid email and password"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot your password?",
            color = Color.Blue,
            modifier = Modifier.clickable { showForgotPasswordDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Don't have an account? Sign Up",
            color = Color.Blue,
            modifier = Modifier.clickable { navController.navigate("signUpScreen") }
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }

        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Forgot Password") },
                text = {
                    Column {
                        TextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Enter your email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                if (resetEmail.isNotEmpty()) {
                                    auth.sendPasswordResetEmail(resetEmail)
                                        .addOnCompleteListener { resetTask ->
                                            if (resetTask.isSuccessful) {
                                                showForgotPasswordDialog = false
                                                errorMessage = "Password reset email sent"
                                            } else {
                                                errorMessage = resetTask.exception?.message ?: "Failed to send reset email"
                                            }
                                        }
                                } else {
                                    errorMessage = "Please enter your email"
                                }
                            }
                        }
                    ) {
                        Text("Reset Password")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showForgotPasswordDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun checkProfileCompletion(user: FirebaseUser, navController: NavController) {
    val db = FirebaseDatabase.getInstance()
    val userRef = db.reference.child("users").child(user.uid)

    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val isProfileComplete = snapshot.child("isProfileComplete").getValue(Boolean::class.java) ?: false
                if (isProfileComplete) {
                    navController.navigate("mainActivity") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
                } else {
                    navController.navigate("profileCompletionScreen") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
                }
            } else {
                navController.navigate("profileCompletionScreen") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            navController.navigate("profileCompletionScreen") {
                popUpTo("loginScreen") { inclusive = true }
            }
        }
    })
}
