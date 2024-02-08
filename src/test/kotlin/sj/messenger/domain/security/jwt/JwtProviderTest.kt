package sj.messenger.domain.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.io.Decoders
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.codec.Utf8


@SpringBootTest
class JwtProviderTest(
    @Autowired
    private val jwtProvider: JwtProvider
) {
    private val objectMapper = ObjectMapper().registerModules(kotlinModule())
    // TODO : Refactoring하기. payload 특정 값만 추출하는걸 따로 분리하던지
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