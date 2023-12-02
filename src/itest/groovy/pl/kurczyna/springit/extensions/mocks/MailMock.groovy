package pl.kurczyna.springit.extensions.mocks

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import jakarta.mail.internet.MimeMessage
import pl.kurczyna.springit.extensions.Mock

import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort

class MailMock implements Mock {

    static int greenMailPort = findAvailableTcpPort()
    static String greenMailUser = UUID.randomUUID().toString()
    static String greenMailPassword = UUID.randomUUID().toString()
    static GreenMail greenMail

    @Override
    void start() {
        greenMail = new GreenMail(ServerSetupTest.SMTP.port(greenMailPort))
                .withConfiguration(
                        GreenMailConfiguration.aConfig()
                                .withUser(greenMailUser, greenMailPassword)
                )
        greenMail.start()
    }

    @Override
    void stop() {
        greenMail.stop()
    }

    @Override
    String[] propertiesToRegister() {
        return [
                "greenmail.port=${greenMailPort}",
                "greenmail.user=${greenMailUser}",
                "greenmail.password=${greenMailPassword}"
        ]
    }

    static MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages()
    }
}
