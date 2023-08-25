package pl.kurczyna.springit.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import pl.kurczyna.springit.thirdparty.Payment

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.springframework.http.HttpStatus.ACCEPTED

class StripeMock {
    WireMockServer server
    ObjectMapper mapper

    StripeMock(WireMockServer server) {
        this.server = server
        this.mapper = new ObjectMapper()
    }

    def payRespondWithSuccess(Payment payment) {
        server.stubFor(post(urlEqualTo("/api/pay"))
                .withRequestBody(equalTo(mapper.writeValueAsString(payment)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(ACCEPTED.value())))
    }

    def verifyPayCalled(Payment payment, int times = 1) {
        server.verify(times, postRequestedFor(urlEqualTo("/api/pay"))
                .withRequestBody(equalTo(mapper.writeValueAsString(payment)))
        )
        true
    }
}
