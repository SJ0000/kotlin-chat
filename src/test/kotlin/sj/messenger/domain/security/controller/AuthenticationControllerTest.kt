package sj.messenger.domain.security.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.JsonPathResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.security.dto.LoginResponse
import sj.messenger.domain.security.jwt.JwtParser
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService
import sj.messenger.util.fixture
import sj.messenger.util.integration.IntegrationTest
import sj.messenger.util.randomEmail
import sj.messenger.util.randomString

@IntegrationTest
class AuthenticationControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userService: UserService,
    @Autowired val om: ObjectMapper,
    @Autowired val jwtParser: JwtParser,
) {

    @Test
    @DisplayName("POST /login : 로그인 성공시 200 OK")
    fun login() {
        // given
        val signUp: SignUpDto = fixture.giveMeOne()
        userService.signUpUser(signUp)

        val loginRequest = LoginRequest(
            email = signUp.email,
            password = signUp.password
        )

        // expected
        val result = mockMvc.post("/login") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("token"){isString()}
                jsonPath("$.user"){exists()}
            }
        }.andReturn()

        // then
        val loginResponse = om.readValue<LoginResponse>(result.response.contentAsString)
        assertThat(loginResponse.user.email).isEqualToIgnoringCase(loginRequest.email)

        val user = userService.findUserByEmail(loginRequest.email)
        val (id, name) = jwtParser.validateAndGetUserClaim(loginResponse.token)
        assertThat(id).isEqualTo(user.id)
        assertThat(name).isEqualTo(user.name)
    }

    @Test
    @DisplayName("POST /login : 이메일이 255자 초과시 400 Bad Request")
    fun loginEmailLengthError() {
        // given
        val loginRequest = LoginRequest(
            email = "${randomString(250)}@${randomString(51)}.com",
            password = randomString(15)
        )

        // expected
        mockMvc.post("/login") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(loginRequest)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.email") {exists()}
            }
        }
    }

    @Test
    @DisplayName("/login : 비밀번호가 10자 미만인 경우 400 Bad Request")
    fun loginPasswordLengthLessThan10() {
        // given
        val passwordLessThan10 = LoginRequest(
            email = randomEmail(),
            password = randomString(9)
        )

        // expected
        mockMvc.post("/login") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(passwordLessThan10)
        }.andExpect {
            status { isBadRequest() }
            content{
                jsonPath("$.fieldErrors.password"){ exists() }
            }
        }
    }

    @Test
    @DisplayName("/login : 비밀번호가 10자 미만인 경우 20자 초과시 400 Bad Request")
    fun loginPasswordLengthGreaterThan20() {
        // given
        val passwordGreaterThan20 = LoginRequest(
            email = randomEmail(),
            password = randomString(21)
        )

        // expected
        mockMvc.post("/login") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(passwordGreaterThan20)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.password") {exists()}
            }
        }
    }
}
