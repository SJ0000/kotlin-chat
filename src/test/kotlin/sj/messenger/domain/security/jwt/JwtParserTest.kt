package sj.messenger.domain.security.jwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JwtParserTest(
    @Autowired
    private val jwtParser: JwtParser,
    @Autowired
    private val jwtProvider: JwtProvider
){
    @Test
    fun validateAndGetUserIdTest(){
        val token = jwtProvider.createAccessToken(UserClaim(id = 1L, name = "TestUser"))

        val userClaim = jwtParser.validateAndGetUserClaim(token)
        assertThat(userClaim).isEqualTo(UserClaim(id = 1L, name = "TestUser"))
    }
}