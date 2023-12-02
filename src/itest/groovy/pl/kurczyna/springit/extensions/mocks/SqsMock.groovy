package pl.kurczyna.springit.extensions.mocks

import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import pl.kurczyna.springit.extensions.Mock
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS

class SqsMock implements Mock {

    private static DockerImageName localstackImage = DockerImageName.parse("localstack/localstack")
    public static LocalStackContainer localstack = new LocalStackContainer(localstackImage).withServices(SQS)

    static String getRegion() {
        return localstack.getRegion()
    }

    static URI getEndpointOverride() {
        return localstack.getEndpointOverride(SQS)
    }

    static AwsCredentialsProvider getCredentials() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.accessKey, localstack.secretKey))
    }

    @Override
    void start() {
        localstack.start()
    }

    @Override
    void stop() {
        localstack.stop()
    }
}
