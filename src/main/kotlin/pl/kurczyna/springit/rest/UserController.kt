package pl.kurczyna.springit.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.kurczyna.springit.User
import pl.kurczyna.springit.db.UserRepository
import pl.kurczyna.springit.kafka.UserBroadcast

@RestController
@RequestMapping("/api/users")
class UserController(private val repository: UserRepository, private val userBroadcast: UserBroadcast) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = repository.getAll()
        return ResponseEntity.ok(users)
    }

    @PostMapping
    fun addOrUpdate(@RequestBody user: User): ResponseEntity<Unit> {
        repository.addOrUpdate(user)
        userBroadcast.broadcastUserRegistration(user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
