package sj.messenger.domain.security.jwt

import org.hibernate.validator.constraints.Length
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import javax.crypto.SecretKey

@Validated
@ConfigurationProperties("jwt")
data class JwtProperties(
    @field:Length(min = 48)
    val secret : String,
    val expirationPeriodMinutes : Long,
)
