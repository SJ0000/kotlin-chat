package sj.messenger.domain.user.controller

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import sj.messenger.domain.security.jwt.JwtProvider
import sj.messenger.domain.security.jwt.UserClaim
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.util.config.WithMockAccessToken
import sj.messenger.util.fixture
import sj.messenger.util.generateUser
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.integration.EnableMockAuthentication


@SpringBootTest
@EnableContainers
@EnableMockAuthentication
@AutoConfigureMockMvc
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userRepository: UserRepository,
) {

    @Test
    @WithMockAccessToken
    fun user() {
        // given
        val user = userRepository.findAll()[0]

        // expected
        mockMvc.get("/users/${user.id!!}"){
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id",user.id)
                jsonPath("name",user.name)
                jsonPath("email",user.email)
                jsonPath("avatarUrl",user.avatarUrl)
                jsonPath("statusMessage",user.statusMessage)
                jsonPath("publicIdentifier",user.publicIdentifier)
            }
        }
    }

    @Test
    fun signUp(){
        // given
        val signUp : SignUpDto = fixture.giveMeOne()

        // expected
        mockMvc.post("/signup"){
            contentType = MediaType.APPLICATION_JSON
            content = signUp
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("name",signUp.name)
                jsonPath("email",signUp.email)
            }
        }
    }
}