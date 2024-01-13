package sj.messenger.domain.security.jwt

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
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
        val token = jwtProvider.createAccessToken(1L)

        val userId = jwtParser.validateAndGetUserId(token)
        assertThat(userId).isEqualTo(1L)
    }
}