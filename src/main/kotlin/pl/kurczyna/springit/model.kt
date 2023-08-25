package pl.kurczyna.springit

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
