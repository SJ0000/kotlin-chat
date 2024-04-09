package sj.messenger.global.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.service.GroupChatService
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.service.UserService

@Profile("local")
@Configuration
class LocalTestDataConfig (
    val userService: UserService,
    val friendRepository: FriendRepository,
    val groupChatService: GroupChatService,
){
    @PostConstruct
    fun createTestAccount(){
        val u1 = userService.signUpUser(SignUpDto("111@123.com", "123", "1234567890"))
        val u2 = userService.signUpUser(SignUpDto("222@123.com", "123", "1234567890"))
        val users = userService.findUsers(listOf(u1, u2))
        friendRepository.save(Friend(users[0],users[1]))

        val groupChatId = groupChatService.createGroupChat(GroupChatCreateDto("test"))
        groupChatService.joinGroupChat(groupChatId, u1)
        groupChatService.joinGroupChat(groupChatId, u2)
    }
}