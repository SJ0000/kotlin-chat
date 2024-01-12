package sj.chat.domain.security.jwt

import jakarta.validation.constraints.Size
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties(
    @Size(min = 34)
    val secret : String,
    val expirationPeriodMinutes : Long,
){
    val expirationPeriodMillis
        get() = expirationPeriodMinutes * 60 * 1000
}
