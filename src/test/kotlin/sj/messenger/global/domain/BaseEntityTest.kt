package sj.messenger.global.domain


import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.JpaRepositoryTest
import sj.messenger.util.generateUser

@JpaRepositoryTest
class BaseEntityTest(
    @Autowired val userRepository: UserRepository
){

    @Test
    fun createdAtTest(){
        // given
        val user = generateUser()
        userRepository.save(user)

        // when


        // then
    }

}