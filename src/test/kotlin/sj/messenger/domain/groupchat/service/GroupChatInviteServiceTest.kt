package sj.messenger.domain.groupchat.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.InvitationRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.ServiceTest
import sj.messenger.util.generateGroupChat
import sj.messenger.util.generateUser
import sj.messenger.util.randomLong
import sj.messenger.util.randomString

@ServiceTest
class GroupChatInviteServiceTest(
    @Autowired val groupChatInviteService: GroupChatInviteService,
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
        val chatRoom = generateGroupChat(user)
        groupChatRepository.save(chatRoom)

        // when
        val invitation = groupChatInviteService.createInvitation(user.id!!, chatRoom.id!!)

        // then
        assertThat(invitationRepository.existsById(invitation.id)).isTrue()
        with(invitation){
            assertThat(groupChatId).isEqualTo(chatRoom.id!!)
            assertThat(inviterId).isEqualTo(user.id!!)
            assertThat(inviterName).isEqualTo(user.name)
        }
    }

    @Test
    @DisplayName("대화방이 존재하지 않는 경우 초대장을 생성할 수 없다.")
    fun createInvitationNotExistsGroupChat(){
        // given
        val user = userRepository.save(generateUser())

        // expected
        assertThatThrownBy {
            groupChatInviteService.createInvitation(user.id!!, 1L)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("대화방이 존재하지 않는 경우 초대장을 생성할 수 없다.")
    fun createInvitationNoParticipant(){
        // given
        val user = userRepository.save(generateUser())
        val otherUser = userRepository.save(generateUser())
        val groupChat = groupChatRepository.save(generateGroupChat(user))

        // expected
        assertThatThrownBy {
            groupChatInviteService.createInvitation(otherUser.id!!,groupChat.id!!)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("초대장 ID로 그룹 채팅 초대장을 조회한다.")
    fun getInvitation(){
        // given
        val invitation = Invitation(randomString(10), randomLong(), randomLong(), randomString(5))
        invitationRepository.save(invitation)

        // when
        val findInvitation = groupChatInviteService.getInvitation(invitation.id)

        // then
        with(findInvitation){
            assertThat(id).isEqualTo(invitation.id)
            assertThat(groupChatId).isEqualTo(invitation.groupChatId)
            assertThat(inviterId).isEqualTo(invitation.inviterId)
            assertThat(inviterName).isEqualTo(invitation.inviterName)
        }
    }

    @Test
    @DisplayName("존재하지 않는 초대장 ID로 그룹 채팅 초대장을 조회하면 RuntimeException 발생")
    fun getInvitationNotFound(){
        assertThatThrownBy {
            groupChatInviteService.getInvitation(randomString(10))
        }.isInstanceOf(RuntimeException::class.java)
    }
}