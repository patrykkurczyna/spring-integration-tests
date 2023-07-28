package pl.kurczyna.springit.utils

import org.intellij.lang.annotations.Language
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import pl.kurczyna.springit.User

import java.sql.ResultSet
import java.sql.SQLException

import static org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils.nextLong

class DbTestClient {

    @Language("PostgreSQL")
    private static final String INSERT_USER = """
            INSERT INTO users (id, name) VALUES (:id, :name)
        """

    @Language("PostgreSQL")
    private static final String GET_USER_BY_ID =
            /
            SELECT *
            FROM users
            WHERE id = :id
        /

    private static def USER_ROW_MAPPER = new RowMapper() {
        @Override
        User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getObject("id", Long.class),
                    rs.getString("name")
            )
        }
    }

    NamedParameterJdbcTemplate template

    DbTestClient(NamedParameterJdbcTemplate template) {
        this.template = template
    }

    void insertUser(Map args = [:]) {
        template.update(INSERT_USER,
                new MapSqlParameterSource([
                        id   : args.id ?: nextLong(),
                        name : args.name ?: 'John Doe'
                ])
        )
    }

    User getUserById(Long id) {
        return template.queryForObject(GET_USER_BY_ID, ['id' : id], USER_ROW_MAPPER) as User
    }
}
