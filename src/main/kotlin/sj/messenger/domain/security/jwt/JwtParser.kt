package sj.messenger.domain.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import javax.crypto.SecretKey

class JwtParser(
    private val secretKey : SecretKey
) {

    private val objectMapper = ObjectMapper().registerModules(kotlinModule())

    // validate, user info parsing
    fun validateAndGetUserClaim(token : String) : UserClaim {
        val payload = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        payload["user"] ?: throw IllegalArgumentException("User Claim이 존재하지 않습니다.")
        val userClaim = objectMapper.readValue<UserClaim>(payload["user"].toString())
        return userClaim
    }
}