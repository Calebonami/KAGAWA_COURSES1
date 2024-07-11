package com.onami.kagawacourses

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.stripe.android.PaymentConfiguration
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentLauncher.Result
import com.stripe.android.payments.paymentlauncher.PaymentLauncherFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    courseTitle: String,
    coursePrice: String,
    navController: NavHostController
) {
    val context = LocalContext.current
    val paymentLauncher = remember { createPaymentLauncher(context) }
    val amount = coursePrice.toInt() * 100 // Convert to cents
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(courseTitle, coursePrice) {
        CoroutineScope(Dispatchers.IO).launch {
            val clientSecret = createPaymentIntent(amount, context)
            clientSecret?.let {
                paymentIntentClientSecret = it
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(text = "Course: $courseTitle")
            Text(text = "Price: $coursePrice")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    paymentIntentClientSecret?.let { clientSecret ->
                        paymentLauncher.confirmPayment(context, clientSecret)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Pay Now")
            }
        }
    }

    handlePaymentResult(paymentLauncher, context)
}

@Composable
fun createPaymentLauncher(context: Context): PaymentLauncher {
    return PaymentLauncherFactory.create(
        context,
        PaymentConfiguration.getInstance(context).publishableKey
    )
}

suspend fun createPaymentIntent(amount: Int, context: Context): String? {
    val client = OkHttpClient()
    val requestBody = JSONObject().put("amount", amount).toString()
    val request = Request.Builder()
        .url("https://your-backend.com/create-payment-intent")
        .post(okhttp3.RequestBody.create(okhttp3.MediaType.get("application/json; charset=utf-8"), requestBody))
        .build()
    return try {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body()?.string() ?: "")
                jsonResponse.getString("clientSecret")
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error creating PaymentIntent", Toast.LENGTH_LONG).show()
                }
                null
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null
    }
}

@Composable
fun handlePaymentResult(paymentLauncher: PaymentLauncher, context: Context) {
    LaunchedEffect(paymentLauncher) {
        paymentLauncher.paymentResult.collect { result ->
            when (result) {
                is Result.Completed -> {
                    Toast.makeText(context, "Payment successful", Toast.LENGTH_LONG).show()
                    // Navigate to the success screen
                }
                is Result.Failed -> {
                    Toast.makeText(context, "Payment failed: ${result.throwable.message}", Toast.LENGTH_LONG).show()
                }
                is Result.Canceled -> {
                    Toast.makeText(context, "Payment canceled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
