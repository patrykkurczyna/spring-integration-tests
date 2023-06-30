package pl.kurczyna.springit.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.sqrt

@RestController
@RequestMapping("/api/math")
class MathController {

    @GetMapping("/sqrt/{number}")
    fun squareRoot(@PathVariable("number") number: Double): ResponseEntity<Double> {
        return ResponseEntity.ok(sqrt(number))
    }
}
