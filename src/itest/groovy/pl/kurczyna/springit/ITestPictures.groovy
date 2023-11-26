package pl.kurczyna.springit

import kotlin.Unit
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpEntity
import org.springframework.util.LinkedMultiValueMap

import static org.springframework.http.HttpStatus.OK

class ITestPictures extends IntegrationTestBase {

    def "should upload picture to GCS"() {
        given: 'There is a picture to be uploaded'
        def picture = new ClassPathResource("pictures/yosemite.png")

        when: 'Upload endpoint is called'
        def body = buildMultipartBody(picture)
        def result = restTemplate.postForEntity('/api/pictures', body, Unit.class)

        then: 'Response is successful'
        result.statusCode == OK

        and: 'Picture is uploaded to GCS'
        def blobs = storageClient.getAllBlobs()
        blobs.size() == 1
        blobs[0].blobId != null
        blobs[0].name == 'yosemite.png'
    }

    private def buildMultipartBody(Resource picture) {
        def body = new LinkedMultiValueMap<>()
        body.add("picture", new HttpEntity<>(picture))
        return body
    }
}
