package sj.messenger.domain.security.jwt

import io.jsonwebtoken.Jwts
import javax.crypto.SecretKey

class JwtParser(
    private val secretKey : SecretKey
) {
    // validate, user info parsing
    fun validateAndGetUserId(token : String) : Long{
        val payload = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        val userId = payload["userId"] as Int
        return userId.toLong()
    }
}