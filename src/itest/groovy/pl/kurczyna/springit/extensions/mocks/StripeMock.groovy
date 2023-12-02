package pl.kurczyna.springit.extensions.mocks

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import pl.kurczyna.springit.extensions.Mock
import pl.kurczyna.springit.thirdparty.Payment

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.springframework.http.HttpStatus.ACCEPTED
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort

class StripeMock implements Mock {
    private static WireMockServer server
    static int stripePort = findAvailableTcpPort()
    static ObjectMapper mapper = new ObjectMapper()

    @Override
    void start() {
        server = new WireMockServer(stripePort)
        server.start()
    }

    @Override
    void cleanup() {
        server.resetAll()
    }

    @Override
    void stop() {
        server.stop()
    }

    @Override
    String[] propertiesToRegister() {
        return [
                "wiremock.stripePort=$stripePort"
        ]
    }

    static payRespondWithSuccess(Payment payment) {
        server.stubFor(post(urlEqualTo("/api/pay"))
                .withRequestBody(equalTo(mapper.writeValueAsString(payment)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(ACCEPTED.value())))
    }

    static payRespondWithFailure(Payment payment) {
        server.stubFor(post(urlEqualTo("/api/pay"))
                .withRequestBody(equalTo(mapper.writeValueAsString(payment)))
                .willReturn(aResponse()
                        .withStatus(INTERNAL_SERVER_ERROR.value())))
    }

    static verifyPayCalled(Payment payment, int times = 1) {
        server.verify(times, postRequestedFor(urlEqualTo("/api/pay"))
                .withRequestBody(equalTo(mapper.writeValueAsString(payment)))
        )
        true
    }
}
