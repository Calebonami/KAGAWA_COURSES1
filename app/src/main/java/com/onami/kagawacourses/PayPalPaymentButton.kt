// PayPalPaymentButton.kt

package com.onami.kagawacourses.ui.payment

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PayPalPaymentButton(
    courseTitle: String,
    coursePrice: String,
    paymentViewModel: PaymentViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    Button(
        onClick = {
            paymentViewModel.createOrder(
                courseTitle = courseTitle,
                coursePrice = coursePrice,
                onSuccess = {
                    Toast.makeText(context, "Payment successful", Toast.LENGTH_LONG).show()
                    navController.navigate("next_screen_route")
                },
                onError = { error ->
                    Toast.makeText(context, "Payment failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(text = "Confirm Payment")
    }
}
