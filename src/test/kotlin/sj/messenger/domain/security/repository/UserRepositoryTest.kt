package sj.messenger.domain.security.repository

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.repository.UserRepository

@DataJpaTest
class UserRepositoryTest (
    @Autowired val userRepository: UserRepository
) {

    @Test
    fun existsByEmailTest() {
        val email = "alpha@beta.com"
        userRepository.save(User("user", email, "1"))
        val result = userRepository.existsByEmail(email)
        assertThat(result).isTrue()
    }

    @Test
    fun findByEmailTest(){
        val email = "alpha@beta.com"
        userRepository.save(User("user",email,"1"))

        val user = userRepository.findByEmail(email)
        assertThat(user?.email).isEqualTo(email)
    }
}