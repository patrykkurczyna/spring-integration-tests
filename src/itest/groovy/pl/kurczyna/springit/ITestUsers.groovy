package pl.kurczyna.springit

import kotlin.Unit
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import static org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils.nextLong

class ITestUsers extends IntegrationTestBase {

    def "should retrieve user list"() {
        given: 'There is one user in the DB'
        Long id = nextLong()
        String name = 'Arthur Morgan'
        dbTestClient.insertUser(id: id, name: name)

        when: 'We fetch users from the API'
        def result = restTemplate.exchange('/api/users', HttpMethod.GET, null,
                new ParameterizedTypeReference<List<User>>() {})

        then: 'List containing one element is returned'
        result.statusCode == HttpStatus.OK
        result.body.size() == 1
        result.body.first() == new User(id, name)
    }

    def "should add new user"() {
        given: 'There is a new user to be added'
        Long id = nextLong()
        User user = new User(id, 'Micah Bell')

        when: 'We call the API to insert user'
        def result = restTemplate.postForEntity('/api/users', new HttpEntity(user), Unit.class)

        then: 'result is success'
        result.statusCode == HttpStatus.CREATED

        and: 'There is a new user in the DB'
        User inDb = dbTestClient.getUserById(id)
        inDb == user
    }

    def "should update user name"() {
        given: 'There is one user in the DB'
        Long id = nextLong()
        String name = 'Arthur Morgan'
        dbTestClient.insertUser(id: id, name: name)

        when: 'We call the API to update user'
        User user = new User(id, 'John Marston')
        def result = restTemplate.postForEntity('/api/users', new HttpEntity(user), Unit.class)

        then: 'result is success'
        result.statusCode == HttpStatus.CREATED

        and: 'Name of the user is updated'
        User inDb = dbTestClient.getUserById(id)
        inDb.name == 'John Marston'
    }
}
