package pl.kurczyna.springit

import spock.util.concurrent.PollingConditions

class ITestSqsEmailEventReceiver extends IntegrationTestBase {

    def "should consume SQS event"() {
        given: 'There is an email event'
        String event = /{
            "id": 13,
            "type": "Marked as spam"
        }/

        when: 'An event is sent to the queue'
        sqsTestClient.sendEvent(event)

        then: 'Event is being processed'
        new PollingConditions(timeout: 5).eventually {
            sqsTestClient.processingEventsSize() == 1
        }

        and: 'Event is successfully consumed'
        new PollingConditions(timeout: 5).eventually {
            sqsTestClient.isQueueEmpty()
        }
    }
}
