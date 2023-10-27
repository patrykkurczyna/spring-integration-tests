package pl.kurczyna.springit.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import pl.kurczyna.springit.BroadcastEvent
import pl.kurczyna.springit.User

@Component
class UserBroadcast(
    @Value("\${topics.broadcast}") private val broadcastTopic: String,
    private val kafkaUsersProducer: KafkaTemplate<String, BroadcastEvent>
) {

    fun broadcastUserRegistration(user: User) {
        kafkaUsersProducer.send(broadcastTopic, user.id.toString(), BroadcastEvent.fromUserEvent(user))
    }
}
