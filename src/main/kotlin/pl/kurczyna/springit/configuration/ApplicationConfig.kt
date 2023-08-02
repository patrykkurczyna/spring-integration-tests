package pl.kurczyna.springit.configuration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.client.RestTemplate
import pl.kurczyna.springit.db.DefaultUserRepository

@Configuration
class ApplicationConfig {

    @Bean
    fun userRepository(jdbcTemplate: NamedParameterJdbcTemplate) = DefaultUserRepository(jdbcTemplate)

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
            restTemplateBuilder
                    .messageConverters(MappingJackson2HttpMessageConverter(jacksonObjectMapper()))
                    .build()
}
