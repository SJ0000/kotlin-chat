package sj.messenger.domain.security.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.fixture.generateUser

@DataJpaTest
class UserRepositoryTest (
    @Autowired val userRepository: UserRepository
) {

    @Test
    @DisplayName("Email을 이용해 사용자 존재 유무 확인")
    fun existsByEmailTest() {
        val user = generateUser()
        userRepository.save(user)
        val result = userRepository.existsByEmail(user.email)
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("Email을 이용한 사용자 조회")
    fun findByEmailTest(){
        val user = generateUser()
        userRepository.save(user)

        val findUser = userRepository.findByEmail(user.email)
        assertThat(findUser).isEqualTo(user)
    }
}