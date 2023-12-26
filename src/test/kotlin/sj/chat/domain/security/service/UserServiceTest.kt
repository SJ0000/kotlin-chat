package sj.chat.domain.security.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import sj.chat.domain.security.dto.SignUp

@SpringBootTest
class UserServiceTest(
    @Autowired
    val userService: UserService
){

    @Test
    fun userTest(){
        val userId = userService.signUpUser(SignUp("alpha@beta.com", "dog", "123"))
        val findUser = userService.findUser(userId)
        print("${findUser.name} ${findUser.email} ${findUser.password}")
    }
}