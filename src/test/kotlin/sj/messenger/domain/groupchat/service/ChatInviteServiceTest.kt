package sj.messenger.domain.groupchat.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.InvitationRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateChatRoom
import sj.messenger.util.generateUser
import sj.messenger.util.testcontainer.initializer.RedisContainerInitializer

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [RedisContainerInitializer::class])
class ChatInviteServiceTest(
    @Autowired val chatInviteService: ChatInviteService,
    @Autowired val userRepository: UserRepository,
    @Autowired val invitationRepository: InvitationRepository,
    @Autowired val groupChatRepository: GroupChatRepository,
){

    @Test
    @DisplayName("대화방의 초대장 생성")
    fun createInvitation(){
        // given
        val user = generateUser()
        userRepository.save(user)
        val chatRoom = generateChatRoom()
        chatRoom.join(user)
        groupChatRepository.save(chatRoom)

        // when
        val invitation = chatInviteService.createInvitation(user.id!!, chatRoom.id!!)

        // then
        assertThat(invitationRepository.existsById(invitation.id)).isTrue()
        with(invitation){
            assertThat(chatRoomId).isEqualTo(chatRoom.id!!)
            assertThat(inviterId).isEqualTo(user.id!!)
            assertThat(inviterName).isEqualTo(user.name)
        }
    }
}