package pl.kurczyna.springit.utils


import java.util.concurrent.ConcurrentHashMap

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class KafkaMock {

    private static final Logger log = LoggerFactory.getLogger(KafkaMock.class)
    private static final String CONFLUENT_PLATFORM_VERSION = '7.4.0'
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName
                    .parse('confluentinc/cp-kafka')
                    .withTag(CONFLUENT_PLATFORM_VERSION)
    private static KafkaContainer container

    static String bootstrapServers

    static ConcurrentHashMap<String, List<Object>> messages = new ConcurrentHashMap<>()

    static void start() {
        log.info("Starting Kafka Container Mock")
        container = new KafkaContainer(KAFKA_IMAGE)
        container.start()
        bootstrapServers = container.getBootstrapServers()
    }

    static void stop() {
        log.info("Stopping Kafka Mock")
        container.stop()
    }

    static void recordMessage(String topic, Object message) {
        def topicMessages = messages.getOrDefault(topic, [])
        topicMessages.add(message)
        messages[topic] = topicMessages
    }
}

