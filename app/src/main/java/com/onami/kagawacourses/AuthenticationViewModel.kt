package com.onami.kagawacourses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    init {
        _isUserLoggedIn.value = auth.currentUser != null
    }

    fun signUpUser(user: User, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            // Check if email, username, or phone number already exists
            database.child("users").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val users = task.result.children.mapNotNull { it.getValue<User>() }
                    val isEmailUsed = users.any { it.email == user.email }
                    val isUsernameUsed = users.any { it.username == user.username }
                    val isPhoneNumberUsed = users.any { it.phoneNumber == user.phoneNumber }

                    if (isEmailUsed) {
                        onComplete(false, "Email is already used")
                    } else if (isUsernameUsed) {
                        onComplete(false, "Username is already used")
                    } else if (isPhoneNumberUsed) {
                        onComplete(false, "Phone number is already used")
                    } else {
                        auth.createUserWithEmailAndPassword(user.email, user.passwordHash)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser = auth.currentUser
                                    firebaseUser?.let {
                                        val userId = it.uid
                                        database.child("users").child(userId).setValue(user)
                                            .addOnCompleteListener { dbTask ->
                                                if (dbTask.isSuccessful) {
                                                    sendEmailVerification(onComplete)
                                                } else {
                                                    onComplete(false, dbTask.exception?.message)
                                                }
                                            }
                                    }
                                } else {
                                    onComplete(false, task.exception?.message)
                                }
                            }
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
        }
    }

    private fun sendEmailVerification(onComplete: (Boolean, String?) -> Unit) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun resendEmailVerification(onComplete: (Boolean) -> Unit) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun isEmailVerified(): Boolean {
        val firebaseUser: FirebaseUser? = auth.currentUser
        firebaseUser?.reload()
        return firebaseUser?.isEmailVerified == true
    }
}
