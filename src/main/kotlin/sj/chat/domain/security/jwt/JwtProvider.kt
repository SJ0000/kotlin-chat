package sj.chat.domain.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

class JwtProvider(
    private val properties : JwtProperties,
) {
    private val registeredClaim = mapOf(
        Claims.ISSUER to "api.simple-messenger",
        Claims.SUBJECT to "access-token",
        Claims.AUDIENCE to "simple-messenger-client"
    )

    private val key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(properties.secret));

    fun createAccessToken(userId: Long): String {
        return Jwts.builder()
            .claims(registeredClaim)
            .claim("userId", userId)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + properties.expirationPeriodMillis))
            .signWith(key)
            .compact()
    }
}