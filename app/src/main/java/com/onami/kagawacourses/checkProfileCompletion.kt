package com.onami.kagawacourses

import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

private fun checkProfileCompletion(user: FirebaseUser, navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users").document(user.uid)

    docRef.get().addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            val isProfileComplete = document.getBoolean("isProfileComplete") ?: false
            if (isProfileComplete) {
                // Profile is complete, navigate to MainActivity and finish login screen
                navController.navigate("mainActivity") {
                    // Clear back stack up to loginScreen
                    popUpTo("loginScreen") { inclusive = true }
                }
            } else {
                // Profile is not complete, show ProfileCompletionScreen
                // No need to navigate here, let LoginScreen handle navigation based on isProfileComplete
            }
        } else {
            // Document doesn't exist, show ProfileCompletionScreen
            // No need to navigate here, let LoginScreen handle navigation based on isProfileComplete
        }
    }.addOnFailureListener {
        // Error fetching document, show ProfileCompletionScreen
        // No need to navigate here, let LoginScreen handle navigation based on isProfileComplete
    }
}
