package pl.kurczyna.springit.db

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import pl.kurczyna.springit.User

interface UserRepository {
    fun getAll(): List<User>
}

class DefaultUserRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : UserRepository {
    override fun getAll(): List<User> {
        TODO("Not yet implemented")
    }
}
