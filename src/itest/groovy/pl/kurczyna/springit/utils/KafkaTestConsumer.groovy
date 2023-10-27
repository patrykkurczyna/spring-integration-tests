package pl.kurczyna.springit.utils


import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@Profile('itest')
class KafkaTestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestConsumer.class)

    @KafkaListener(topics = ['\${topics.broadcast}'])
    static void receiveBroadcastMessage(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info('Received broadcast kafka message: {}', consumerRecord.value())
        KafkaMock.recordMessage(consumerRecord.topic(), consumerRecord.value())
    }

    static <T>List<T> getTopicMessages(String topic) {
        KafkaMock.messages[topic].collect { it as T }
    }
}
