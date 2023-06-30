package pl.kurczyna.springit.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.kurczyna.springit.User
import pl.kurczyna.springit.db.UserRepository

@RestController
@RequestMapping("/api/users")
class UserController(private val repository: UserRepository) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = repository.getAll()
        return ResponseEntity.ok(users)
    }
}
