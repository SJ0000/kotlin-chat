package sj.messenger.global.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.friend.service.FriendService
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService

@Profile("local")
@Configuration
class LocalTestDataConfig (
    val userService: UserService,
    val friendRepository: FriendRepository,
){
    @PostConstruct
    fun createTestAccount(){
        val u1 = userService.signUpUser(SignUpDto("111@123.com", "123", "1234567890"))
        val u2 = userService.signUpUser(SignUpDto("222@123.com", "123", "1234567890"))
        val users = userService.findUsers(listOf(u1, u2))
        friendRepository.save(Friend(users[0],users[1]))
    }
}