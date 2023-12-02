package pl.kurczyna.springit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import pl.kurczyna.springit.configuration.GcsITestConfiguration
import pl.kurczyna.springit.extensions.MockEnvironment
import pl.kurczyna.springit.extensions.Mocks
import pl.kurczyna.springit.extensions.mocks.SqsMock
import pl.kurczyna.springit.utils.DbTestClient
import pl.kurczyna.springit.utils.SqsTestClient
import pl.kurczyna.springit.utils.StorageTestClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static pl.kurczyna.springit.extensions.Service.GCS
import static pl.kurczyna.springit.extensions.Service.KAFKA
import static pl.kurczyna.springit.extensions.Service.MAIL
import static pl.kurczyna.springit.extensions.Service.SQS
import static pl.kurczyna.springit.extensions.Service.STRIPE

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles('itest')
@Import(GcsITestConfiguration)
@ContextConfiguration(initializers = PropertyInitializer)
@Mocks(services = [GCS, KAFKA, MAIL, SQS, STRIPE] )
class IntegrationTestBase extends Specification {

    @LocalServerPort
    int appPort

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    KafkaTemplate<String, UserEvent> kafkaUsersProducer

    @Autowired
    StorageTestClient storageClient

    @Autowired
    SqsTestClient sqsTestClient

    @Autowired
    DbTestClient dbTestClient

    def setup() {
        sqsTestClient.createQueue()
    }

    static class PropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    MockEnvironment.propertiesToRegister()
            )
        }
    }

    @TestConfiguration
    static class AwsITestConfiguration {

        @Bean
        @Primary
        SqsAsyncClient amazonSQSAsync() {
            return SqsAsyncClient.builder()
                    .region(Region.of(SqsMock.getRegion()))
                    .credentialsProvider(SqsMock.getCredentials())
                    .endpointOverride(SqsMock.getEndpointOverride())
                    .build()
        }
    }
}
