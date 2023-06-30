package pl.kurczyna.springit.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import pl.kurczyna.springit.db.DefaultUserRepository

@Configuration
class ApplicationConfig {

    @Bean
    fun userRepository(jdbcTemplate: NamedParameterJdbcTemplate) = DefaultUserRepository(jdbcTemplate)
}
