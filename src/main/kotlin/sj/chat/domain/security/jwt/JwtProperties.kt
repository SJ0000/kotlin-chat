package sj.chat.domain.security.jwt

import org.hibernate.validator.constraints.Length
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("jwt")
class JwtProperties(
    @field:Length(min = 48)
    val secret : String,
    val expirationPeriodMinutes : Long,
){
    val expirationPeriodMillis
        get() = expirationPeriodMinutes * 60 * 1000
}
