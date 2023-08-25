package pl.kurczyna.springit

import org.springframework.beans.factory.annotation.Value
import spock.util.concurrent.PollingConditions

import static org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils.nextLong

class ITestUserEventsKafka extends IntegrationTestBase {

    @Value("\${topics.users}")
    String usersTopic

    def "should store user in db when registration event comes"() {
        given: 'There is a user event'
        Long id = nextLong()
        String name = 'John Lundstram'
        UserEvent event = new UserEvent(id, Operation.REGISTER, name)

        when: 'The event is sent to Kafka'
        kafkaUsersProducer.send(usersTopic, id.toString(), event)

        then: 'Within 5 seconds the user is register in the db. Note that we test asynchronous code'
        new PollingConditions(timeout: 5).eventually {
            User inDb = dbTestClient.getUserById(id)
            inDb.id == id
            inDb.name == name
        }
    }

    def "should delete user from db when delete event comes"() {
        given: 'There is already one user in the DB'
        Long id = nextLong()
        String name = 'John Lundstram'
        dbTestClient.insertUser(id: id, name: name)

        and: 'There is a delete user event'
        UserEvent event = new UserEvent(id, Operation.DELETE, name)

        when: 'The event is sent to Kafka'
        kafkaUsersProducer.send(usersTopic, id.toString(), event)

        then: 'Within 5 seconds the user is deleted from the db. Note that we test asynchronous code'
        new PollingConditions(timeout: 5).eventually {
            User inDb = dbTestClient.getUserById(id)
            inDb == null
        }
    }
}
