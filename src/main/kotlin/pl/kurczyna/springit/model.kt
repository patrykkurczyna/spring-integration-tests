package pl.kurczyna.springit

import java.time.Instant

data class User(
    val id: Long,
    val name: String
)

data class UserEvent(
    val id: Long,
    val operation: Operation,
    val name: String
) {
    fun toUser() = User(id, name)
}

enum class Operation {
    REGISTER, DELETE
}

data class BroadcastEvent(
    val userId: Long,
    val timestamp: Instant
) {
    companion object {
        fun fromUserEvent(user: User) = BroadcastEvent(user.id, Instant.now())
    }
}
