package sj.messenger.domain.security.jwt

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sj.messenger.util.fixture
import javax.crypto.SecretKey

class JwtParserTest(){
    private val secret = Arbitraries.strings().alpha().ofLength(48).sample()
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))
    private val jwtParser: JwtParser = JwtParser(key)
    private val jwtProvider: JwtProvider = JwtProvider(key, 240000)

    @Test
    fun validateAndGetUserIdTest(){
        val token = jwtProvider.createAccessToken(UserClaim(id = 1L, name = "TestUser"))

        val userClaim = jwtParser.validateAndGetUserClaim(token)
        assertThat(userClaim).isEqualTo(UserClaim(id = 1L, name = "TestUser"))
    }
}