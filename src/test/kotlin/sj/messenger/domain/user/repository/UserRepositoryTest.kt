package sj.messenger.domain.user.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import sj.messenger.RepositoryTest
import sj.messenger.util.generateUser
import sj.messenger.global.config.QueryDslConfig

@RepositoryTest
class UserRepositoryTest (
    @Autowired val userRepository: UserRepository
) {
    @Test
    fun temp(){
        val generateUser = generateUser()
        println("[before save] userId = ${generateUser.id}")
        userRepository.save(generateUser)
        println("[after save] userId = ${generateUser.id}")
        val findUser = userRepository.findByEmail(generateUser.email)
        println("[after find] userId = ${findUser?.id}")
    }

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