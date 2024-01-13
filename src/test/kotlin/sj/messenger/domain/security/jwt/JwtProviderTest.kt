package sj.messenger.domain.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.io.Decoders
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.codec.Utf8


@SpringBootTest
class JwtProviderTest(
    @Autowired
    private val jwtProvider: JwtProvider
) {

    // TODO : Refactoring하기. payload 특정 값만 추출하는걸 따로 분리하던지
    @Test
    fun createToken() {
        val token = jwtProvider.createAccessToken(1L)
        val (header,payload,signature) = token.split(".")
        println(payload)
        val bytes = Decoders.BASE64URL.decode(payload)
        val payloadJson = Utf8.decode(bytes)
        println(payloadJson)
        val om = ObjectMapper()
        val map = om.readValue(payloadJson, Map::class.java)
        assertThat(map["userId"]).isEqualTo(1)
    }
}