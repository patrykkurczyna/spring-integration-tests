package pl.kurczyna.springit.configuration

import com.google.cloud.NoCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class GcsITestConfiguration {

    @Value("\${gcs.port}")
    private String gcsContainerPort

    @Bean
    @Primary
    Storage gcsStorage() {
        return StorageOptions.newBuilder()
                .setHost("http://localhost:" + gcsContainerPort)
                .setProjectId("itest-project")
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService()
    }
}
