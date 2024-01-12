package sj.chat.domain.security.repository

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import sj.chat.domain.user.domain.User
import sj.chat.domain.user.repository.UserRepository

@DataJpaTest
class UserRepositoryTest (
    @Autowired val userRepository: UserRepository
){

    @Test
    fun existsByEmailTest(){
        val email = "alpha@beta.com"
        userRepository.save(User("user",email,"1"))
        val result = userRepository.existsByEmail(email)
        assertThat(result).isTrue()
    }
}