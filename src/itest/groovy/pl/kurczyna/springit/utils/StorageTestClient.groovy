package pl.kurczyna.springit.utils

import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class StorageTestClient {
    @Autowired
    Storage storageClient
    @Value("\${gcs.bucket}")
    private String bucketName

    def createBucket() {
        def bucketInfo = BucketInfo.newBuilder(bucketName).build()
        storageClient.create(bucketInfo)
    }

    def deleteBucket() {
        storageClient.list(bucketName).streamAll().forEach {it -> storageClient.delete(it.blobId)}
        storageClient.delete(bucketName)
    }

    def getAllBlobs() {
        return storageClient.list(bucketName).values
    }
}
