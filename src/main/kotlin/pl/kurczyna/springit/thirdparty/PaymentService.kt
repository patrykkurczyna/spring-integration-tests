package pl.kurczyna.springit.thirdparty

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.Currency

data class Payment(
    val amount: Long,
    val currency: Currency,
    val paymentMethod: String
)

interface PaymentService {
    fun makePayment(payment: Payment)
}

@Service
class StripePaymentService(
    private val template: RestTemplate,
    @Value("\${payment.url}") private val paymentServiceUrl: String
) : PaymentService {
    override fun makePayment(payment: Payment) {
        template.postForEntity("$paymentServiceUrl/api/pay", HttpEntity(payment), Unit::class.java)
    }
}
