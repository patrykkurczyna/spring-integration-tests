package pl.kurczyna.springit

import org.springframework.boot.test.web.server.LocalManagementPort

class ITestManagement extends IntegrationTestBase {

    @LocalManagementPort
    int mgmtPort

    def "should return #expectedStatus status for management path: #path"() {
        when:
        HttpURLConnection connection = new URL("http://localhost:$mgmtPort/_/health").openConnection() as HttpURLConnection

        then:
        connection.getResponseCode() == 200
    }
}
