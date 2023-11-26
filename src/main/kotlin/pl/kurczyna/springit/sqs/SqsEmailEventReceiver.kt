package pl.kurczyna.springit.sqs

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

data class EmailEvent(
    val id: Long,
    val type: String
)

@Service
class SqsEmailEventReceiver {
    private companion object {
        val log: Logger = LoggerFactory.getLogger(SqsEmailEventReceiver::class.java)
    }

    @SqsListener(value = ["\${sqs.queue-name}"])
    fun handle(event: EmailEvent) {
        log.info("Handling email event: $event finished with success")
    }
}
