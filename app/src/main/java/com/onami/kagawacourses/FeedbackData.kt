package com.onami.kagawacourses

data class FeedbackData(
    val id: String,
    val name: String,
    val email: String,
    val feedbackType: String,
    val message: String,
    val attachmentUri: String
)
