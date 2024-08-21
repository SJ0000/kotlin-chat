package sj.messenger.util

import io.jsonwebtoken.Jwts
import sj.messenger.domain.security.jwt.JwtProvider
import java.util.Date
import javax.crypto.SecretKey

class TestJwtProvider(
    secretKey: SecretKey,
    expirationPeriodMillis : Long
) : JwtProvider(secretKey, expirationPeriodMillis){

    fun createAccessTokenWithoutUserClaim(): String{
        return Jwts.builder()
            .claims(registeredClaim)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationPeriodMillis ))
            .signWith(secretKey)
            .compact()
    }
}