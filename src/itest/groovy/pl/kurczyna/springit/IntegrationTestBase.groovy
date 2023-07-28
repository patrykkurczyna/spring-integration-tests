package pl.kurczyna.springit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.kurczyna.springit.utils.DbTestClient
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles('itest')
@ContextConfiguration
abstract class IntegrationTestBase extends Specification {

    @LocalServerPort
    int appPort

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private NamedParameterJdbcTemplate template

    DbTestClient dbTestClient

    def setup() {
        dbTestClient = new DbTestClient(template)
    }
}
