package pl.kurczyna.springit

import spock.lang.Specification

class SimpleSpec extends Specification {

    def "should add 2 numbers"() {
        expect:
        2 + 7 == 9
    }
}
