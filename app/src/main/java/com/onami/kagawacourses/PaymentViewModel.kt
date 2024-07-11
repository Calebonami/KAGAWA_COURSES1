// PaymentViewModel.kt

package com.onami.kagawacourses.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onami.kagawacourses.data.repository.PaymentRepository
import kotlinx.coroutines.launch

class PaymentViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {

    fun createOrder(
        courseTitle: String,
        coursePrice: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val orderId = paymentRepository.createOrder(courseTitle, coursePrice)
                // Optionally handle orderId if needed
                onSuccess.invoke()
            } catch (e: Exception) {
                onError.invoke(e)
            }
        }
    }
}
