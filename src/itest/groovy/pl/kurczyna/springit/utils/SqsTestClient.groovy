package pl.kurczyna.springit.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.*

import static java.lang.Integer.parseInt
import static software.amazon.awssdk.services.sqs.model.QueueAttributeName.ALL

@Component
class SqsTestClient {
    @Autowired
    private SqsAsyncClient sqsClient
    @Value("\${sqs.queue-name}") private String queueName

    def createQueue() {
        sqsClient.createQueue(CreateQueueRequest.builder().queueName(queueName).build() as CreateQueueRequest)
    }

    def sendEvent(String body) {
        sqsClient.sendMessage(getMessageRequest(body))
    }

    def processingEventsSize() {
        parseInt(getQueueAttributes().get("ApproximateNumberOfMessagesNotVisible"))
    }

    def isQueueEmpty() {
        def attributes = getQueueAttributes()
        return parseInt(attributes.get("ApproximateNumberOfMessagesNotVisible")) +
            parseInt(attributes.get("ApproximateNumberOfMessages")) +
            parseInt(attributes.get("ApproximateNumberOfMessagesDelayed")) == 0
    }

    private SendMessageRequest getMessageRequest(String body) {
        SendMessageRequest.builder().queueUrl(queueName).messageBody(body).build() as SendMessageRequest
    }

    private Map<String, String> getQueueAttributes() {
        def request = GetQueueAttributesRequest
            .builder()
            .queueUrl(queueName)
            .attributeNames(ALL)
            .build() as GetQueueAttributesRequest

        sqsClient.getQueueAttributes(request).get().attributesAsStrings()
    }
}
