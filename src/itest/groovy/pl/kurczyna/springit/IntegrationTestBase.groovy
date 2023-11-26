package pl.kurczyna.springit

import com.github.tomakehurst.wiremock.WireMockServer
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import pl.kurczyna.springit.configuration.GcsITestConfiguration
import pl.kurczyna.springit.utils.DbTestClient
import pl.kurczyna.springit.utils.GcsMock
import pl.kurczyna.springit.utils.KafkaMock
import pl.kurczyna.springit.utils.SqsTestClient
import pl.kurczyna.springit.utils.StorageTestClient
import pl.kurczyna.springit.utils.StripeMock
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles('itest')
@Import(GcsITestConfiguration)
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

    @Autowired
    StorageTestClient storageClient

    @Autowired
    SqsTestClient sqsTestClient

    DbTestClient dbTestClient

    private static WireMockServer stripeServer
    StripeMock stripeMock
    static int stripePort = findAvailableTcpPort()

    static int greenMailPort = findAvailableTcpPort()
    static String greenMailUser = UUID.randomUUID().toString()
    static String greenMailPassword = UUID.randomUUID().toString()
    static GreenMail greenMail

    private static DockerImageName localstackImage = DockerImageName.parse("localstack/localstack")
    public static LocalStackContainer localstack = new LocalStackContainer(localstackImage).withServices(SQS)

    def setupSpec() {
        stripeServer = new WireMockServer(stripePort)
        stripeServer.start()
        KafkaMock.start()
        GcsMock.start()
        greenMail = new GreenMail(ServerSetupTest.SMTP.port(greenMailPort))
                .withConfiguration(
                        GreenMailConfiguration.aConfig()
                                .withUser(greenMailUser, greenMailPassword)
                )
        localstack.start()
    }

    def setup() {
        dbTestClient = new DbTestClient(template)
        stripeMock = new StripeMock(stripeServer)
        storageClient.createBucket()
        greenMail.start()
        sqsTestClient.createQueue()
    }

    def cleanup() {
        stripeServer.resetAll()
        storageClient.deleteBucket()
        greenMail.stop()
    }

    def cleanupSpec() {
        stripeServer.stop()
//        KafkaMock.stop()
//        GcsMock.stop()
        localstack.stop()
    }

    static class PropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext applicationContext) {
            String[] properties = [
                    "wiremock.stripePort=$stripePort",
                    "kafka.bootstrapServers=${KafkaMock.bootstrapServers}",
                    "gcs.port=${GcsMock.gcsPort}",
                    "greenmail.port=${greenMailPort}",
                    "greenmail.user=${greenMailUser}",
                    "greenmail.password=${greenMailPassword}"
            ]
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    properties
            )
        }
    }

    @TestConfiguration
    static class AwsITestConfiguration {

        @Bean
        @Primary
        SqsAsyncClient amazonSQSAsync() {
            return SqsAsyncClient.builder()
                    .region(Region.of(localstack.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.accessKey, localstack.secretKey)))
                    .endpointOverride(localstack.getEndpointOverride(SQS))
                    .build()
        }
    }
}
