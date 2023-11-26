package pl.kurczyna.springit.utils

import net.minidev.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.testcontainers.containers.GenericContainer

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GcsMock {

    private static def container
    static def gcsPort

    static void start() {
        container = new GenericContainer<>("fsouza/fake-gcs-server:latest")
                .withExposedPorts(4443)
                .withCreateContainerCmdModifier {
                    it.withEntrypoint(
                            "/bin/fake-gcs-server",
                            "-scheme", "http")
                }
        container.start()
        gcsPort = container.getFirstMappedPort()
        updateExternalUrlWithContainerUrl(container.getHost(), gcsPort)
    }

    static void stop() {
        container.stop()
    }

    static String[] propertiesToRegister() {
        return ["gcs.port=$gcsPort"]
    }

    private static def updateExternalUrlWithContainerUrl(String host, Integer port) throws Exception {
        def fakeGcsExternalUrl = "http://" + host + ":" + port
        def modifyExternalUrlRequestUri = fakeGcsExternalUrl + "/_internal/config"
        def updateExternalUrlJson = new JSONObject()
        updateExternalUrlJson.put("externalUrl", fakeGcsExternalUrl)

        def request = HttpRequest.newBuilder()
                .uri(URI.create(modifyExternalUrlRequestUri))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .PUT(HttpRequest.BodyPublishers.ofString(updateExternalUrlJson.toString()))
                .build()
        def response = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.discarding())

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new RuntimeException("Error updating fake-gcs-server with external URL, response status code ${response.statusCode()} != 200")
        }
    }
}
