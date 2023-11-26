package pl.kurczyna.springit.thirdparty

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.ByteBuffer
import java.util.*

interface PicturesService {
    fun uploadPicture(picture: MultipartFile)
}

@Service
class DefaultPicturesService(
    @Value("\${gcs.bucket}") private val bucketName: String,
    val storage: Storage
) : PicturesService {
    override fun uploadPicture(picture: MultipartFile) {
        val blobId = BlobId.of(bucketName, picture.originalFilename)
        val blobInfo = BlobInfo.newBuilder(blobId).apply {
            setContentType(picture.contentType)
        }.build()
        val content = picture.bytes

        storage.writer(blobInfo).use { writer ->
            writer.write(ByteBuffer.wrap(content, 0, content.size))
        }

        blobInfo.blobId.name
    }
}
