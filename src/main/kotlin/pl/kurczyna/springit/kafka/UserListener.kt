package pl.kurczyna.springit.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import pl.kurczyna.springit.Operation
import pl.kurczyna.springit.UserEvent
import pl.kurczyna.springit.db.UserRepository

@Component
class UserListener(private val repository: UserRepository) {

    @KafkaListener(topics = ["\${topics.users}"])
    fun handleUserEvent(event: UserEvent) {
        when (event.operation) {
            Operation.REGISTER -> repository.addOrUpdate(event.toUser())
            Operation.DELETE -> repository.delete(event.id)
        }
    }
}
