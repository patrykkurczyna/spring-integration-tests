package pl.kurczyna.springit

import com.icegreen.greenmail.util.GreenMailUtil
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import pl.kurczyna.springit.extensions.mocks.MailMock
import pl.kurczyna.springit.thirdparty.Email
import pl.kurczyna.springit.thirdparty.EmailService

class ITestEmailService extends IntegrationTestBase {
    @Autowired
    EmailService emailService

    def "should send e-mail"() {
        given: 'There is an email to be sent'
        def email = new Email('anakin@skywalker.com', 'luke@skywalker.com', 'Luke!', 'I am your father')

        when: 'The email is sent'
        emailService.sendEmail(email)

        then: 'Greenmail receives one email message'
        def messages = MailMock.getReceivedMessages()
        messages.size() == 1
        with(messages.first()) {
            getHeader(it, 'From') == email.sender
            getHeader(it, 'To') == email.recipient
            getHeader(it, 'Subject') == email.subject
            def body = GreenMailUtil.getBody(it)
            body.contains(email.content)
        }
    }

    private static def getHeader(MimeMessage message, String header) {
        message.allHeaders.find { it.name == header }.value
    }
}
