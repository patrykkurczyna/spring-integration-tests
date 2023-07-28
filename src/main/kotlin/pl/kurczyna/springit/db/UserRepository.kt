package pl.kurczyna.springit.db

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import pl.kurczyna.springit.User

interface UserRepository {
    fun getAll(): List<User>
    fun addOrUpdate(user: User)
}

class DefaultUserRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : UserRepository {
    private companion object {
        const val GET_ALL_USERS_QUERY = "SELECT * FROM users;"
        const val UPSERT_USER_QUERY = """              
                    INSERT INTO users (id, name) values (:id, :name)
                    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name
        """
    }

    override fun getAll(): List<User> =
        jdbcTemplate.query(GET_ALL_USERS_QUERY) { rs, _ -> User(rs.getLong("id"), rs.getString("name")) }

    override fun addOrUpdate(user: User) {
        jdbcTemplate.update(UPSERT_USER_QUERY, mapOf("id" to user.id, "name" to user.name))
    }
}
