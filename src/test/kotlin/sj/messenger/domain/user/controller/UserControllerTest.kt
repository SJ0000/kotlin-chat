package sj.messenger.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.dto.UpdateUserDto
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.global.exception.ErrorCode
import sj.messenger.util.annotation.IntegrationTest
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.fixture
import sj.messenger.util.randomEmail
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
        mockMvc.get("/users/${user.id!!}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id") { value(user.id) }
                jsonPath("name") { value(user.name) }
                jsonPath("email") { value(user.email) }
                jsonPath("avatarUrl") { value(user.avatarUrl) }
                jsonPath("statusMessage") { value(user.statusMessage) }
                jsonPath("publicIdentifier") { value(user.publicIdentifier) }
            }
        }
    }

    @Test
    fun signUp() {
        // given
        val signUp: SignUpDto = fixture.giveMeOne()

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(signUp)
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("name") { value(signUp.name) }
                jsonPath("email") { value(signUp.email.lowercase()) }
            }
        }
    }

    @Test
    @DisplayName("회원가입시 대소문자만 다른 이메일도 중복으로 처리한다.")
    fun signUpEmailIgnoreCase() {
        // given
        val upperEmail = "ABCD@EFGH.com"

        val lowerEmailSignUp = SignUpDto(
            email = upperEmail.lowercase(),
            name = randomString(5, 10),
            password = randomString(5, 10)
        )
        userService.signUpUser(lowerEmailSignUp)

        val upperEmailSignUp = SignUpDto(
            email = upperEmail,
            name = randomString(5, 10),
            password = randomString(5, 10)
        )

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(upperEmailSignUp)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("POST /signup : email 형식 불일치시 400 bad request")
    fun signUpEmailFormatError() {
        // given
        val emailInvalidFormat = SignUpDto(
            email = "email_invalid_format",
            name = randomString(10),
            password = randomString(15)
        );

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(emailInvalidFormat)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.email") { exists() }
            }
        }
    }

    @Test
    @DisplayName("POST /signup : email 길이 255자 초과시 400 bad request")
    fun signUpEmailLengthGreaterThan255() {
        // given
        val emailLengthGT255 = SignUpDto(
            email = "${randomString(255)}@123.com",
            name = randomString(10),
            password = randomString(15)
        )

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(emailLengthGT255)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.email") { exists() }
            }
        }
    }

    @Test
    @DisplayName("POST /signup : name 길이 20자 초과시 400 bad request")
    fun signUpNameLengthGreaterThan20() {
        // given
        val nameLengthGT20 = SignUpDto(
            email = randomEmail(),
            name = randomString(21),
            password = randomString(15)
        )

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(nameLengthGT20)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.name") { exists() }
            }
        }
    }

    @Test
    @DisplayName("POST /signup : password 길이 10자 미만시 400 bad request")
    fun signUpPasswordLengthLessThan10() {
        // given
        val passwordLengthLT10 = SignUpDto(
            email = randomEmail(),
            name = randomString(15),
            password = randomString(9)
        )

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(passwordLengthLT10)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.password") { exists() }
            }
        }
    }

    @Test
    @DisplayName("POST /signup : password 길이 20자 초과시 400 bad request")
    fun signUpPasswordLengthGreaterThan20() {
        // given
        val passwordLengthGT20 = SignUpDto(
            email = randomEmail(),
            name = randomString(15),
            password = randomString(21)
        )

        // expected
        mockMvc.post("/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(passwordLengthGT20)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.password") { exists() }
            }
        }
    }

    @Test
    @InjectAccessToken
    fun patchUser() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser: UpdateUserDto = fixture.giveMeOne()

        // expected
        mockMvc.patch("/users/${user.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("name") { value(updateUser.name) }
                jsonPath("avatarUrl") { value(updateUser.avatarUrl) }
                jsonPath("statusMessage") { value(updateUser.statusMessage) }
                jsonPath("publicIdentifier") { value(updateUser.publicIdentifier) }
            }
        }
    }

    @Test
    @InjectAccessToken
    @DisplayName("PATCH /users/{id} : name이 255자 초과인 경우 400 Bad Request")
    fun patchUserNameGT255() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser: UpdateUserDto = fixture.giveMeOne()
        ReflectionTestUtils.setField(updateUser, "name", randomString(256))

        // expected
        mockMvc.patch("/users/${user.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.name") { exists() }
            }
        }
    }

    @Test
    @InjectAccessToken
    @DisplayName("PATCH /users/{id} : 자신이 아닌 다른 user 수정 시도시 401 Unauthorized")
    fun patchUserUnauthorized() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser: UpdateUserDto = fixture.giveMeOne()

        // expected
        mockMvc.patch("/users/${user.id!!+1}") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isUnauthorized() }
            content {
                jsonPath("$.code") { value(ErrorCode.HAS_NO_PERMISSION.code) }
                jsonPath("$.message") { value(ErrorCode.HAS_NO_PERMISSION.message) }
            }
        }
    }

    @Test
    @InjectAccessToken
    @DisplayName("PATCH /users/{id} : name이 빈 문자열일 경우 400 Bad Request")
    fun patchUserNameBlank() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser: UpdateUserDto = fixture.giveMeOne()
        ReflectionTestUtils.setField(updateUser, "name", "  ")

        // expected
        mockMvc.patch("/users/${user.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.name") { exists() }
            }
        }
    }

    @Test
    @InjectAccessToken
    @DisplayName("PATCH /users/{id} : statusMessage 255자 초과시 400 Bad Request")
    fun patchUserStatusMessageGT255() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser: UpdateUserDto = fixture.giveMeOne()
        ReflectionTestUtils.setField(updateUser, "statusMessage", randomString(256))

        // expected
        mockMvc.patch("/users/${user.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.statusMessage") { exists() }
            }
        }
    }

    @Test
    @InjectAccessToken
    @DisplayName("PATCH /users/{id} : publicIdentifier 255자 초과시 400 Bad Request")
    fun patchUserPublicIdentifierGT255() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val updateUser: UpdateUserDto = fixture.giveMeOne()
        ReflectionTestUtils.setField(updateUser, "publicIdentifier", randomString(256))

        // expected
        mockMvc.patch("/users/${user.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(updateUser)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.publicIdentifier") { exists() }
            }
        }
    }
}