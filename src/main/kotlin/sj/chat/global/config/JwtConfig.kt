package sj.chat.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sj.chat.domain.security.jwt.JwtProperties
import sj.chat.domain.security.jwt.JwtProvider

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig(
    private val properties: JwtProperties,
) {
    @Bean
    fun jwtProvider(): JwtProvider {
        return JwtProvider(properties)
    }

}