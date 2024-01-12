package sj.chat.domain.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtProviderTest(){

    private val jwtProvider = JwtProvider()

    @Test
    fun createToken(){
        val secret = "12345678901234567890123456789012345678901234567890"
        val expirationPeriod = 24 * 60 * 60 * 1000
        val token = jwtProvider.createAccessToken(1L)

        val jws = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret)))
            .build()
            .parseSignedClaims(token)

        val userId = jws.payload["userId"]
        assertThat(userId).isEqualTo(1)
    }
}