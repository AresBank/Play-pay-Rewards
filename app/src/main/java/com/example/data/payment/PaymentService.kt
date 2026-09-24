package com.example.data.payment

import android.content.Context
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Production-ready PaymentService to orchestrate Mercado Pago & PayPal payouts,
 * validation, and status tracking for real-world disbursements.
 */
class PaymentService(
    private val context: Context,
    private val repository: AppRepository? = null
) {
    val gatewayService = PaymentGatewayService(context)

    suspend fun processWithdrawal(
        methodType: String,
        amountMxn: Double,
        destination: String,
        holderName: String,
        userEmail: String
    ): PaymentResult = withContext(Dispatchers.IO) {
        val result = when (methodType.uppercase()) {
            "MERCADO_PAGO", "MERCADOPAGO", "SPEI" -> {
                gatewayService.executeMercadoPagoPayout(
                    amountMxn = amountMxn,
                    clabeOrAccount = destination,
                    holderName = holderName,
                    userEmail = userEmail
                )
            }
            "PAYPAL" -> {
                gatewayService.executePayPalPayout(
                    amountMxn = amountMxn,
                    recipientEmail = destination
                )
            }
            else -> {
                PaymentResult(
                    success = false,
                    status = "FAILED",
                    transactionReference = "ERR-UNKNOWN",
                    trackingKey = "N/A",
                    provider = methodType,
                    destination = destination,
                    amountMxn = amountMxn,
                    message = "Método de pago no soportado: $methodType"
                )
            }
        }

        result
    }

    fun getConfiguration(): PaymentGatewayConfig {
        return gatewayService.getConfig()
    }

    fun updateConfiguration(config: PaymentGatewayConfig) {
        gatewayService.saveConfig(config)
    }
}
