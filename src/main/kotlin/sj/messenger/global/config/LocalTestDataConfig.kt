package sj.messenger.global.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService

@Profile("local")
@Configuration
class LocalTestDataConfig (
    val userService: UserService
){
    @PostConstruct
    fun createTestAccount(){
        userService.signUpUser(SignUpDto("123@123.com","123","1234567890"))
    }
}