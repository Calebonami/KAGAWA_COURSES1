package com.onami.kagawacourses

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SecurityQuestionsScreen(navController: NavController) {
    val context = LocalContext.current
    var question1 by remember { mutableStateOf("") }
    var answer1 by remember { mutableStateOf("") }
    var question2 by remember { mutableStateOf("") }
    var answer2 by remember { mutableStateOf("") }
    var question3 by remember { mutableStateOf("") }
    var answer3 by remember { mutableStateOf("") }

    fun saveSecurityQuestions() {
        // Implement the logic to save the security questions and answers
        Toast.makeText(context, "Security questions saved", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Set Security Questions",
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Question 1
        OutlinedTextField(
            value = question1,
            onValueChange = { question1 = it },
            label = { Text("Question 1") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = answer1,
            onValueChange = { answer1 = it },
            label = { Text("Answer 1") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Question 2
        OutlinedTextField(
            value = question2,
            onValueChange = { question2 = it },
            label = { Text("Question 2") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = answer2,
            onValueChange = { answer2 = it },
            label = { Text("Answer 2") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Question 3
        OutlinedTextField(
            value = question3,
            onValueChange = { question3 = it },
            label = { Text("Question 3") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = answer3,
            onValueChange = { answer3 = it },
            label = { Text("Answer 3") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                saveSecurityQuestions()
                navController.popBackStack() // Navigate back to the previous screen
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
