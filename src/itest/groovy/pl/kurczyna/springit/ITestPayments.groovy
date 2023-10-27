package pl.kurczyna.springit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.HttpServerErrorException
import pl.kurczyna.springit.thirdparty.Payment
import pl.kurczyna.springit.thirdparty.PaymentService

class ITestPayments extends IntegrationTestBase {

    @Autowired
    PaymentService paymentService

    def "should make a successful payment using Stripe"() {
        given: 'Payment is prepared'
        Payment payment = new Payment(100, Currency.getInstance('PLN'), "VISA")

        and: 'Stripe responds with success on new payment call'
        stripeMock.payRespondWithSuccess(payment)

        when: 'We make a payment'
        paymentService.makePayment(payment)

        then: 'No exception is thrown'
        noExceptionThrown()

        and: 'Stripe pay endpoint was called once with specific data'
        stripeMock.verifyPayCalled(payment, 1)
    }

    def "should throw exception when Stripe API call fails"() {
        given: 'Payment is prepared'
        Payment payment = new Payment(100, Currency.getInstance('PLN'), "VISA")

        and: 'Stripe responds with failure'
        stripeMock.payRespondWithFailure(payment)

        when: 'We make a payment'
        paymentService.makePayment(payment)

        then: 'An exception is thrown'
        thrown(HttpServerErrorException.InternalServerError)

        and: 'Stripe pay endpoint was called once with specific data'
        stripeMock.verifyPayCalled(payment, 1)
    }
}
