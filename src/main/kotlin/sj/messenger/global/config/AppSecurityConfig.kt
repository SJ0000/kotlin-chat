package sj.messenger.global.config

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.hibernate.validator.constraints.Length
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import sj.messenger.domain.security.jwt.JwtParser
import sj.messenger.domain.security.jwt.JwtProvider
import javax.crypto.SecretKey

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties::class)
class AppSecurityConfig(
    private val properties: JwtProperties,
) {

    private val secretKey: SecretKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(properties.secret)) }

    @Bean
    fun jwtProvider(): JwtProvider {
        val expirationPeriodMillis = properties.expirationPeriodMinutes * 60 * 1000
        return JwtProvider(secretKey, expirationPeriodMillis)
    }

    @Bean
    fun jwtParser(): JwtParser {
        return JwtParser(secretKey)
    }

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

@Validated
@ConfigurationProperties("app.security.jwt")
data class JwtProperties(
    @field:Length(min = 48)
    val secret : String,
    val expirationPeriodMinutes : Long,
)