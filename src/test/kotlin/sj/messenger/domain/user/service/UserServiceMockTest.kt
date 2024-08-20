package sj.messenger.domain.user.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.fixture

/**
 *  Spring Container 필요 없이 Mocking 필요한 테스트
 */
@ExtendWith(MockitoExtension::class)
class UserServiceMockTest {

    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var passwordEncoder: PasswordEncoder
    @InjectMocks
    lateinit var userService: UserService

    @Test
    @DisplayName("publicIdentifer 생성에 실패한 경우 RuntimeException 발생")
    fun createPublicIdentifierFail() {
        // given
        BDDMockito.`when`(userRepository.existsByEmail(Mockito.anyString()))
            .thenReturn(false)
        BDDMockito.`when`(userRepository.existsByPublicIdentifier(Mockito.anyString()))
            .thenReturn(true)

        // expected
        Assertions.assertThatThrownBy {
            userService.signUpUser(fixture.giveMeOne(SignUpDto::class.java))
        }.isInstanceOf(RuntimeException::class.java)
    }
}