package pl.kurczyna.springit.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import pl.kurczyna.springit.thirdparty.PicturesService

@RestController
@RequestMapping("/api/pictures")
class PicturesController(private val picturesService: PicturesService) {

    @PostMapping
    fun uploadPicture(
        @RequestPart("picture", required = true)
        picture: MultipartFile
    ): ResponseEntity<Unit> {
        picturesService.uploadPicture(picture)
        return ResponseEntity.ok().build()
    }
}
