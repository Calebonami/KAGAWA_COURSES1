package com.onami.kagawacourses

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun SignUpScreen(navController: NavController) {
    val viewModel: AuthenticationViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showVerificationMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val validationResult = validateInput(username, email, phoneNumber, password, confirmPassword)
                    if (validationResult == null) {
                        val passwordHash = hashPassword(password)
                        val user = User(username, email, phoneNumber, passwordHash)
                        viewModel.signUpUser(user) { success, message ->
                            if (success) {
                                showVerificationMessage = true
                            } else {
                                errorMessage = message ?: "An unknown error occurred"
                            }
                        }
                    } else {
                        errorMessage = validationResult
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showVerificationMessage) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("A verification email has been sent to your email address. Please verify your email to continue.", color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.resendEmailVerification { success ->
                            if (success) {
                                errorMessage = "Verification email resent. Please check your email."
                            } else {
                                errorMessage = "Failed to resend verification email."
                            }
                        }
                    }
                ) {
                    Text("Resend Verification Email")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (viewModel.isEmailVerified()) {
                            navController.navigate("loginScreen")
                        } else {
                            errorMessage = "Please verify your email before proceeding."
                        }
                    }
                ) {
                    Text("Proceed to Login")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Already have an account? Sign In",
            color = Color.Blue,
            modifier = Modifier.clickable {
                navController.navigate("loginScreen")
            }
        )
    }
}

fun validateInput(
    username: String,
    email: String,
    phoneNumber: String,
    password: String,
    confirmPassword: String
): String? {
    if (username.isEmpty()) {
        return "Username is required"
    }
    if (!isValidEmail(email)) {
        return "Invalid email address"
    }
    if (!isValidPhoneNumber(phoneNumber)) {
        return "Invalid phone number"
    }
    if (password.isEmpty() || password.length < 8) {
        return "Password must be at least 8 characters long"
    }
    if (password != confirmPassword) {
        return "Passwords do not match"
    }
    return null
}

fun isValidEmail(email: String): Boolean {
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    )
    return emailPattern.matcher(email).matches()
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phonePattern = Pattern.compile(
        "^\\+[1-9]{1}[0-9]{3,14}\$"
    )
    return phonePattern.matcher(phoneNumber).matches()
}
