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
        val users = (1..3).map { generateUser() }
        userRepository.saveAll(users)

        val userIds= users.map { it.id }.toMutableList()
        userIds.add(999L)

        val result = userRepository.findAllById(userIds)
        println(result.size)
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
        assertThat(findUser?.id).isEqualTo(user.id)
    }
}