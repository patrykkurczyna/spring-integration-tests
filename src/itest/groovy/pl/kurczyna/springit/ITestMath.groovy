package pl.kurczyna.springit

import org.springframework.http.HttpStatus

class ITestMath extends IntegrationTestBase {

    def "should calculate square root"() {
        given:
        double number = 9.0

        when:
        def result = restTemplate.getForEntity("/api/math/sqrt/$number", Double.class)

        then:
        result.statusCode == HttpStatus.OK
        result.body == 3.0
    }
}
