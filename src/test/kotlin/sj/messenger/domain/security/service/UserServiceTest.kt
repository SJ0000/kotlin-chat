package sj.messenger.domain.security.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService

@SpringBootTest
class UserServiceTest(
    @Autowired
    val userService: UserService
){

    @Test
    fun userTest(){
        val userId = userService.signUpUser(SignUpDto("alpha@beta.com", "dog", "123"))
        val findUser = userService.findUser(userId)
        print("${findUser.name} ${findUser.email} ${findUser.password}")
    }

}