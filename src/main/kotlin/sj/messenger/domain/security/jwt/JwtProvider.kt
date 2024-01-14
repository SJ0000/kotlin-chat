package sj.messenger.domain.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import sj.messenger.domain.security.authentication.UserToken
import java.util.*
import javax.crypto.SecretKey

class JwtProvider(
    private val secretKey : SecretKey,
    private val expirationPeriodMillis : Long,
) {
    private val registeredClaim = mapOf(
        Claims.ISSUER to "api.simple-messenger",
        Claims.SUBJECT to "access-token",
        Claims.AUDIENCE to "simple-messenger-client"
    )

    fun createAccessToken(userToken: UserToken): String {
        return Jwts.builder()
            .claims(registeredClaim)
            .claim("user", userToken)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationPeriodMillis))
            .signWith(secretKey)
            .compact()
    }
}