package sj.messenger.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.dto.UpdateUserDto
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.fixture
import sj.messenger.util.integration.IntegrationTest
import sj.messenger.util.randomString


@IntegrationTest
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userService: UserService,
    @Autowired val userRepository: UserRepository,
    @Autowired val om: ObjectMapper,
) {

    @Test
    @InjectAccessToken
    fun user() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!

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
            content = om.writeValueAsString(signUp)
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("name",signUp.name)
                jsonPath("email",signUp.email)
            }
        }
    }

    @Test
    @DisplayName("회원가입시 대소문자만 다른 이메일도 중복으로 처리한다.")
    fun signUpEmailIgnoreCase(){
        // given
        val upperEmail = "ABCD@EFGH.com"

        val lowerEmailSignUp = SignUpDto(
            email = upperEmail.lowercase(),
            name = randomString(5,10),
            password = randomString(5,10)
        )
        userService.signUpUser(lowerEmailSignUp)

        val upperEmailSignUp = SignUpDto(
            email = upperEmail,
            name = randomString(5,10),
            password = randomString(5,10)
        )

        // expected
        mockMvc.post("/signup"){
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(upperEmailSignUp)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @InjectAccessToken
    fun patchUser(){
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser : UpdateUserDto = fixture.giveMeOne()

        // expected
        mockMvc.patch("/users/${user.id!!}"){
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("name",updateUser.name)
                jsonPath("avatarUrl",updateUser.avatarUrl)
                jsonPath("statusMessage",updateUser.statusMessage)
                jsonPath("publicIdentifier",updateUser.publicIdentifier)
            }
        }
    }
}