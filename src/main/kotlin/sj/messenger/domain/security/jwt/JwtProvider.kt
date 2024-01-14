package sj.messenger.domain.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.util.*
import javax.crypto.SecretKey

class JwtProvider(
    private val secretKey : SecretKey,
    private val expirationPeriodMillis : Long,
) {
    private val objectMapper = ObjectMapper().registerModules(kotlinModule())

    private val registeredClaim = mapOf(
        Claims.ISSUER to "api.simple-messenger",
        Claims.SUBJECT to "access-token",
        Claims.AUDIENCE to "simple-messenger-client"
    )

    fun createAccessToken(userClaim: UserClaim): String {
        return Jwts.builder()
            .claims(registeredClaim)
            .claim("user", objectMapper.writeValueAsString(userClaim))
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationPeriodMillis))
            .signWith(secretKey)
            .compact()
    }
}