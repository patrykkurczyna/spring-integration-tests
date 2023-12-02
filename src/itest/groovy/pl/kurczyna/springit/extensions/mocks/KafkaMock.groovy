package pl.kurczyna.springit.extensions.mocks

import pl.kurczyna.springit.extensions.Mock

import java.util.concurrent.ConcurrentHashMap

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class KafkaMock implements Mock {

    private static final Logger log = LoggerFactory.getLogger(KafkaMock.class)
    private static final String CONFLUENT_PLATFORM_VERSION = '7.4.0'
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName
                    .parse('confluentinc/cp-kafka')
                    .withTag(CONFLUENT_PLATFORM_VERSION)
    private static KafkaContainer container

    static String bootstrapServers

    static ConcurrentHashMap<String, List<Object>> messages = new ConcurrentHashMap<>()

    @Override
    void start() {
        log.info("Starting Kafka Container Mock")
        container = new KafkaContainer(KAFKA_IMAGE)
        container.start()
        bootstrapServers = container.getBootstrapServers()
    }

    @Override
    void stop() {
        log.info("Stopping Kafka Mock")
        container.stop()
    }

    @Override
    String[] propertiesToRegister() {
        return [
                "kafka.bootstrapServers=$bootstrapServers"
        ]
    }

    static void recordMessage(String topic, Object message) {
        def topicMessages = messages.getOrDefault(topic, [])
        topicMessages.add(message)
        messages[topic] = topicMessages
    }
}

