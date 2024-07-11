package com.onami.kagawacourses

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

private val Any.h6: TextStyle
    get() {
        return TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }

private val Any.h4: TextStyle
    get() {
        return TextStyle(
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }

@Composable
fun ProfileCompletionScreen(navController: NavController) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val countryList = getCountryList()
    val selectedCountry = remember { mutableStateOf(TextFieldValue("")) }
    val address = remember { mutableStateOf(TextFieldValue("")) }
    val biography = remember { mutableStateOf(TextFieldValue("")) }
    val isBiographyError = remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri.value = uri }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Profile Completion", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture Upload
        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            imageUri.value?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(text = "Upload\nPhoto", textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        // Country Selection
        Text(text = "Country", style = MaterialTheme.typography.h6)
        AutoCompleteTextView(countryList, selectedCountry)

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        // Address Input
        Text(text = "Address", style = MaterialTheme.typography.h6)
        OutlinedTextField(
            value = address.value,
            onValueChange = { address.value = it },
            label = { Text("Enter your address") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        // Biography Input
        Text(text = "Biography", style = MaterialTheme.typography.h6)
        OutlinedTextField(
            value = biography.value,
            onValueChange = {
                biography.value = it
                isBiographyError.value = it.text.length < 100 || it.text.length > 500
            },
            label = { Text("Enter your biography") },
            isError = isBiographyError.value,
            modifier = Modifier.height(200.dp),
            placeholder = { Text("Enter your detailed biography here...") }
        )
        if (isBiographyError.value) {
            Text(
                text = "Biography must be between 100 and 500 words.",
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        // Submit Button
        Button(onClick = {
            if (imageUri.value != null && biography.value.text.length in 100..500) {
                uploadImageToFirebaseStorage(imageUri.value!!) { url ->
                    if (url != null) {
                        val profileData = ProfileData(
                            profilePictureUrl = url,
                            country = selectedCountry.value.text,
                            address = address.value.text,
                            biography = biography.value.text
                        )
                        saveUserProfileToDatabase(userId = FirebaseAuth.getInstance().currentUser!!.uid, profileData) {
                            // Set profile completion flag
                            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                            userRef.child("isProfileComplete").setValue(true).addOnCompleteListener {
                                // Navigate to MainActivity
                                navController.navigate("mainActivity") {
                                    popUpTo("profileCompletionScreen") { inclusive = true }
                                }
                            }
                        }
                    } else {
                        // Show error message
                    }
                }
            } else {
                // Show validation error message
            }
        }) {
            Text("Submit")
        }
    }
}

@Composable
fun AutoCompleteTextView(countryList: List<Country>, selectedCountry: MutableState<TextFieldValue>) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box {
        OutlinedTextField(
            value = selectedCountry.value,
            onValueChange = { selectedCountry.value = it },
            label = { Text("Select Country") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { expanded = it.isFocused }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryList.filter { it.name.contains(selectedCountry.value.text, true) }.forEach { country ->
                DropdownMenuItem(onClick = {
                    selectedCountry.value = TextFieldValue(country.name)
                    expanded = false
                    focusManager.clearFocus()
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberImagePainter(country.flag),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${country.name} (${country.code})")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuItem(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        content()
    }
}

// Function to upload image to Firebase Storage
fun uploadImageToFirebaseStorage(uri: Uri, onComplete: (String?) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/${UUID.randomUUID()}")
    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { url ->
                onComplete(url.toString())
            }
        }
        .addOnFailureListener {
            onComplete(null)
        }
}

// Function to save user profile data to Firebase Realtime Database
fun saveUserProfileToDatabase(userId: String, profileData: ProfileData, onComplete: () -> Unit) {
    val databaseRef = FirebaseDatabase.getInstance().reference.child("profiles").child(userId)
    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId) // To store email optionally

    // Update profile data in profiles node
    databaseRef.setValue(profileData)
        .addOnSuccessListener {
            // Optionally update email in users node (if needed)
            val userData = HashMap<String, Any>()
            userData["email"] = FirebaseAuth.getInstance().currentUser!!.email!!  // Assuming email is retrieved

            userRef.updateChildren(userData)
                .addOnSuccessListener {
                    onComplete()
                }
                .addOnFailureListener {
                    // Handle error updating email
                }
        }
        .addOnFailureListener {
            onComplete()  // Still call onComplete even if email update fails
        }
}

// Data class for profile data
data class ProfileData(
    val profilePictureUrl: String,
    val country: String,
    val address: String,
    val biography: String
)

