package sj.messenger.domain.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.codec.Utf8
import javax.crypto.SecretKey


class JwtProviderTest{
    private val secret = Arbitraries.strings().alpha().ofLength(48).sample()
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))
    private val jwtProvider: JwtProvider = JwtProvider(key, 240000)

    private val objectMapper = ObjectMapper().registerModules(kotlinModule())

    @Test
    fun createToken() {
        val userClaim = UserClaim(id = 1L, name = "TestUser")
        val token = jwtProvider.createAccessToken(userClaim)
        val (header,payload,signature) = token.split(".")
        println(payload)
        val bytes = Decoders.BASE64URL.decode(payload)
        val payloadJson = Utf8.decode(bytes)
        println(payloadJson)
        val map = objectMapper.readValue(payloadJson, Map::class.java)
        val claimInToken = objectMapper.readValue<UserClaim>(map["user"].toString())
        assertThat(claimInToken).isEqualTo(userClaim)
    }
}