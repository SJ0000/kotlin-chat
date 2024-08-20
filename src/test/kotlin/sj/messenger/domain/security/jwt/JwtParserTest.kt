package sj.messenger.domain.security.jwt

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import sj.messenger.util.TestJwtProvider
import javax.crypto.SecretKey

class JwtParserTest(){
    private val secret = Arbitraries.strings().alpha().ofLength(48).sample()
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))
    private val jwtParser: JwtParser = JwtParser(key)
    private val jwtProvider: TestJwtProvider = TestJwtProvider(key, 240000)

    @Test
    fun validateAndGetUserClaimTest(){
        // given
        val token = jwtProvider.createAccessToken(UserClaim(id = 1L, name = "TestUser"))

        // when
        val userClaim = jwtParser.validateAndGetUserClaim(token)

        // then
        assertThat(userClaim).isEqualTo(UserClaim(id = 1L, name = "TestUser"))
    }

    @Test
    fun validateAndGetUserClaimPayloadError(){
        // given
        val token = jwtProvider.createAccessTokenWithoutUserClaim()

        // expected
        Assertions.assertThatThrownBy {
            jwtParser.validateAndGetUserClaim(token)
        }.isInstanceOf(RuntimeException::class.java)
    }
}