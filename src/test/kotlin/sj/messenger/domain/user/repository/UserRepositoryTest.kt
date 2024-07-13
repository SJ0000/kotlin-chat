package sj.messenger.domain.user.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.util.annotation.JpaRepositoryTest
import sj.messenger.util.generateUser

@JpaRepositoryTest
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
    @DisplayName("PublicIdentifier를 이용해 사용자 존재 유무 확인")
    fun existsByPublicIdentifierTest() {
        val user = generateUser()
        userRepository.save(user)
        val result = userRepository.existsByPublicIdentifier(user.publicIdentifier)
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("Email을 이용한 사용자 조회")
    fun findByEmailTest(){
        val user = generateUser()
        userRepository.save(user)

        val findUser = userRepository.findByEmail(user.email)
        assertThat(findUser?.id).isEqualTo(user.id)
    }

    @Test
    @DisplayName("PublicIdentifier를 이용한 사용자 조회")
    fun findByPublicIdentifierTest() {
        val user = generateUser()
        userRepository.save(user)
        val result = userRepository.findByPublicIdentifier(user.publicIdentifier)
        assertThat(result?.id).isEqualTo(user.id)
    }
}