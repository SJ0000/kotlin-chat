package sj.messenger.domain.security.jwt

import io.jsonwebtoken.Jwts
import sj.messenger.domain.security.authentication.UserToken
import javax.crypto.SecretKey

class JwtParser(
    private val secretKey : SecretKey
) {
    // validate, user info parsing
    fun validateAndGetUserId(token : String) : UserToken{
        val payload = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        return payload["user"] as UserToken
    }
}