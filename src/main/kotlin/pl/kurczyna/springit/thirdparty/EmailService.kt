package pl.kurczyna.springit.thirdparty

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

data class Email(
    val sender: String,
    val recipient: String,
    val subject: String,
    val content: String
)

interface EmailService {
    fun sendEmail(email: Email)
}

@Service
class SmtpEmailService(private val javaMailSender: JavaMailSender) : EmailService {
    override fun sendEmail(email: Email) {
        val mimeMessage = javaMailSender.createMimeMessage()
        MimeMessageHelper(mimeMessage, true).apply {
            setText(email.content, true)
            setTo(email.recipient)
            setFrom(email.sender)
            setSubject(email.subject)
        }
        javaMailSender.send(mimeMessage)
    }
}
