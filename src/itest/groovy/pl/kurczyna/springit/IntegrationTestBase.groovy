package pl.kurczyna.springit

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import pl.kurczyna.springit.utils.DbTestClient
import pl.kurczyna.springit.utils.KafkaMock
import pl.kurczyna.springit.utils.StripeMock
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles('itest')
@ContextConfiguration(initializers = PropertyInitializer)
abstract class IntegrationTestBase extends Specification {

    @LocalServerPort
    int appPort

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private NamedParameterJdbcTemplate template

    @Autowired
    KafkaTemplate<String, UserEvent> kafkaUsersProducer

    DbTestClient dbTestClient

    private static WireMockServer stripeServer
    StripeMock stripeMock
    static int stripePort = findAvailableTcpPort()

    def setupSpec() {
        stripeServer = new WireMockServer(stripePort)
        stripeServer.start()
        KafkaMock.start()
    }

    def setup() {
        dbTestClient = new DbTestClient(template)
        stripeMock = new StripeMock(stripeServer)
    }

    def cleanup() {
        stripeServer.resetAll()
    }

    def cleanupSpec() {
        stripeServer.stop()
        KafkaMock.stop()
    }

    static class PropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext applicationContext) {
            String[] properties = ["wiremock.stripePort=$stripePort", "kafka.bootstrapServers=${KafkaMock.bootstrapServers}"]
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    properties
            )
        }
    }
}
