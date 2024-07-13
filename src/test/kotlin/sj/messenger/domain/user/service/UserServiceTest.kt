package sj.messenger.domain.user.service

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import sj.messenger.domain.security.dto.LoginRequest
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.dto.UpdateUserDto
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.*
import sj.messenger.util.annotation.ServiceTest

@ServiceTest
class UserServiceTest(
    @Autowired val userService: UserService,
    @Autowired val userRepository: UserRepository,
    @Autowired val passwordEncoder: PasswordEncoder,
) {

    @Test
    @DisplayName("findUserById()에 존재하지 않는 user의 id가 입력된 경우 예외 발생")
    fun findUserByIdError() {
        // expected
        assertThatThrownBy {
            userService.findUserById(9999)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("findUserById()에 존재하지 않는 user의 id가 입력된 경우 예외 발생")
    fun findUserByEmailError() {
        // expected
        assertThatThrownBy {
            userService.findUserByEmail(randomEmail())
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("findUserById()에 존재하지 않는 user의 id가 입력된 경우 예외 발생")
    fun findUserByPublicIdentifierError() {
        // expected
        assertThatThrownBy {
            userService.findUserByPublicIdentifier(fixture.giveMeOne())
        }.isInstanceOf(RuntimeException::class.java)
    }


    @Test
    @DisplayName("findUsers는 입력으로 받은 userId 리스트와 일치하는 User 리스트를 반환한다.")
    fun findUsers() {
        // given
        val users = (1..3).map { generateUser() }
        userRepository.saveAll(users)

        // when
        val userIds = users.map { it.id!! }.toSet()
        val findUsers = userService.findUsers(userIds)

        // then
        assertThat(findUsers.size).isEqualTo(users.size)
        assertThat(findUsers.map { it.id!! }).containsAnyElementsOf(userIds)
    }

    @Test
    @DisplayName("findUsers는 여러 사용자의 정보를 조회했을 때, 존재하지 않는 사용자가 있을 경우 예외 발생")
    fun findUsersError() {
        // given
        val users = (1..3).map { generateUser() }
        userRepository.saveAll(users)
        val notExistsUserId = users.maxOf { it.id!! } + 1

        // expected
        assertThatThrownBy {
            userService.findUsers(users.map { it.id!! }.toSet() + setOf(notExistsUserId))
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    fun signUpUser() {
        // given
        val dto = SignUpDto(randomEmail(), randomString(5,10), randomString(10,15))

        // when
        val userId = userService.signUpUser(dto)

        // then
        val user = userRepository.findByIdOrNull(userId)!!
        assertThat(user.name).isEqualTo(dto.name)
        assertThat(user.email).isEqualTo(dto.email)
        assertThat(user.password).isNotEqualTo(dto.password)
    }

    @Test
    @DisplayName("로그인 요청시 비밀번호가 일치할 경우 예외 발생하지 않음")
    fun validateLogin() {
        // given
        val rawPassword: String = randomString(10,20)
        val encryptedPassword = passwordEncoder.encode(rawPassword)
        val user = User(
            name = randomString(5,255),
            email = randomEmail(),
            password = encryptedPassword,
            publicIdentifier = randomString(20,20)
        )
        userRepository.save(user)
        val loginRequest = LoginRequest(user.email, rawPassword)
        // expected
        assertDoesNotThrow { userService.validateLogin(loginRequest) }
    }

    @Test
    @DisplayName("로그인 요청시 비밀번호가 일치하지 않을 경우 예외 발생")
    fun validateLoginError() {
        // given
        val rawPassword = randomPassword()
        val encryptedPassword = passwordEncoder.encode(rawPassword)
        val user = User(
            name = randomString(5,255),
            email = randomEmail(),
            password = encryptedPassword,
            publicIdentifier = randomString(5,255)
        )
        userRepository.save(user)
        val loginRequest = LoginRequest(user.email, rawPassword+"1")

        // expected
        assertThatThrownBy { userService.validateLogin(loginRequest) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("사용자의 정보를 업데이트")
    fun updateUser() {
        // given
        val user = generateUser()
        userRepository.save(user)

        val dto = UpdateUserDto(
            name = randomString(5,25),
            avatarUrl = randomUrl(),
            statusMessage = randomString(5,255),
            publicIdentifier = randomString(5,255)
        )

        // when
        userService.updateUser(user.id!!,dto)

        // then
        with(userRepository.findByIdOrNull(user.id!!)!!){
            assertThat(name).isEqualTo(dto.name)
            assertThat(avatarUrl).isEqualTo(dto.avatarUrl)
            assertThat(statusMessage).isEqualTo(dto.statusMessage)
            assertThat(publicIdentifier).isEqualTo(dto.publicIdentifier)
        }
    }

    @Test
    @DisplayName("사용자 정보 수정시 PublicIdentifier는 유일해야 한다.")
    fun updateUserError() {
        // given
        val user = generateUser()
        val user2 = generateUser()
        userRepository.saveAll(listOf(user,user2))

        val dto = UpdateUserDto(
            name = randomString(1,255),
            avatarUrl = randomUrl(),
            statusMessage = randomString(0,255),
            publicIdentifier = user2.publicIdentifier
        )

        // expected
        assertThatThrownBy { userService.updateUser(user.id!!,dto) }
            .isInstanceOf(RuntimeException::class.java)
    }
}