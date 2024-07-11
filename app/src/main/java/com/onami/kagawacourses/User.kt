package com.onami.kagawacourses

data class User(
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val passwordHash: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val address: String = ""
)