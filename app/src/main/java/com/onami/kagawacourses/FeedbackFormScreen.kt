package com.onami.kagawacourses

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class FeedbackFormActivity : AppCompatActivity() {

    private lateinit var feedbackRef: DatabaseReference
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var feedbackTypeEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var attachButton: Button
    private lateinit var submitButton: Button
    private lateinit var attachedFileNameTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var attachmentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_form)

        feedbackRef = Firebase.database.getReference("feedback")

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        feedbackTypeEditText = findViewById(R.id.feedbackTypeEditText)
        messageEditText = findViewById(R.id.messageEditText)
        attachButton = findViewById(R.id.attachButton)
        submitButton = findViewById(R.id.submitButton)
        attachedFileNameTextView = findViewById(R.id.attachedFileNameTextView)
        progressBar = findViewById(R.id.progressBar)

        attachButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_ATTACHMENT)
        }

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val feedbackType = feedbackTypeEditText.text.toString()
            val message = messageEditText.text.toString()

            if (validateForm(name, email, feedbackType, message)) {
                progressBar.visibility = View.VISIBLE
                submitButton.isEnabled = false
                submitFeedback(
                    this,
                    name,
                    email,
                    feedbackType,
                    message,
                    attachmentUri,
                    feedbackRef
                ) { success, error ->
                    progressBar.visibility = View.GONE
                    submitButton.isEnabled = true
                    if (success) {
                        Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                        // Navigate to success screen
                    } else {
                        Toast.makeText(this, error ?: "Failed to submit feedback. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_ATTACHMENT && resultCode == Activity.RESULT_OK) {
            attachmentUri = data?.data
            attachedFileNameTextView.text = "Attached: ${attachmentUri?.lastPathSegment}"
        }
    }

    private fun validateForm(name: String, email: String, feedbackType: String, message: String): Boolean {
        return name.isNotBlank() && email.isNotBlank() && feedbackType.isNotBlank() && message.isNotBlank()
    }

    private fun submitFeedback(
        context: Context,
        name: String,
        email: String,
        feedbackType: String,
        message: String,
        attachmentUri: Uri?,
        feedbackRef: DatabaseReference,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val feedbackId = UUID.randomUUID().toString()
        val feedbackData = FeedbackData(
            id = feedbackId,
            name = name,
            email = email,
            feedbackType = feedbackType,
            message = message,
            attachmentUri = attachmentUri.toString()
        )
        feedbackRef.child(feedbackId).setValue(feedbackData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception?.message)
            }
    }

    companion object {
        private const val REQUEST_CODE_PICK_ATTACHMENT = 1001
    }
}
