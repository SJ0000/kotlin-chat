package sj.messenger

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService


@SpringBootApplication
class MessengerApplication(
	private val userService: UserService
){
	@Profile("local")
	@PostConstruct
	fun createTestAccount(){
		userService.signUpUser(SignUpDto("123@123.com","123","1234567890"))
	}
}

fun main(args: Array<String>) {
	runApplication<MessengerApplication>(*args)
}



