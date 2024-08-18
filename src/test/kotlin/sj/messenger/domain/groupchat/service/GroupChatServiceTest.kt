package sj.messenger.domain.groupchat.service

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.domain.GroupChatRole
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.dto.ParticipantUpdateDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.ParticipantRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.*
import sj.messenger.util.annotation.ServiceTest

@ServiceTest
class GroupChatServiceTest(
    @Autowired val groupChatService: GroupChatService,
    @Autowired val userRepository: UserRepository,
    @Autowired val groupChatRepository: GroupChatRepository,
    @Autowired val participantRepository: ParticipantRepository,
) {

    @Test
    @DisplayName("존재하지 않는 GroupChat 조회시 예외 발생")
    fun getGroupChatError() {
        // expected
        assertThatThrownBy { groupChatService.getGroupChat(9999) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("사용자가 대화방에 정상적으로 참여")
    fun joinGroupChat() {
        // given
        val groupChatCreator = userRepository.save(generateUser())
        val groupChat = groupChatRepository.save(GroupChat.create(groupChatCreator, fixture.giveMeOne()))

        val newUser = userRepository.save(generateUser())
        // when
        groupChatService.joinGroupChat(groupChat.id!!, newUser.id!!)

        // then
        val isParticipant = groupChatRepository.findByIdOrNull(groupChat.id!!)?.isParticipant(groupChatCreator.id!!)
        assertThat(isParticipant).isTrue()
    }

    @Test
    @DisplayName("사용자가 참여하고자 하는 대화방에 이미 참여중인 경우 예외 발생")
    fun joinChatRoomError() {
        // given
        val user = userRepository.save(generateUser())
        val groupChat = groupChatRepository.save(GroupChat.create(user, name = randomString(1, 255)))

        // expected
        assertThatThrownBy {
            groupChatService.joinGroupChat(groupChat.id!!, user.id!!)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("대화방 정상 생성")
    fun createChatRoom() {
        // given
        val user = userRepository.save(generateUser())
        val groupChatCreateDto = fixture.giveMeOne<GroupChatCreateDto>()

        // when
        val chatRoomId = groupChatService.createGroupChat(user.id!!, groupChatCreateDto)

        // then
        val findGroupChat = groupChatRepository.findByIdOrNull(chatRoomId)
        assertThat(findGroupChat).isNotNull
        assertThat(findGroupChat?.name).isEqualTo(groupChatCreateDto.name)
    }

    @Test
    @DisplayName("특정 사용자의 참가한 대화방 리스트 조회")
    fun findUserGroupChats() {
        // given
        val user = userRepository.save(generateUser())
        val groupChats = (1..3).map { generateGroupChat(user) }
        groupChatRepository.saveAll(groupChats)

        // when
        val userGroupChats = groupChatService.findUserGroupChats(user.id!!)

        // then
        assertThat(userGroupChats.size).isEqualTo(groupChats.size)
    }

    @Test
    @DisplayName("단일 GroupChat 조회")
    fun findGroupChat() {
        // given
        val user = userRepository.save(generateUser())
        val groupChat = groupChatRepository.save(generateGroupChat(user))

        // when
        val result = groupChatService.findGroupChat(groupChat.id!!)

        // then
        assertThat(result.id).isEqualTo(groupChat.id)
        assertThat(result.name).isEqualTo(groupChat.name)
        assertThat(result.isParticipant(user.id!!)).isTrue()
    }

    @Test
    @DisplayName("단일 GroupChat 조회시 없다면 RuntimeException 발생")
    fun findGroupChatNotFound() {
        // expected
        assertThatThrownBy {
            groupChatService.findGroupChat(randomLong())
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("ADMIN은 대화방 참여자의 권한을 수정할 수 있다.")
    fun updateParticipant() {
        // given
        val admin = userRepository.save(generateUser())
        val member = userRepository.save(generateUser())
        val groupChat = generateGroupChat(admin)
        groupChat.join(member)
        groupChatRepository.save(groupChat)

        // when
        val participantId = groupChat.getParticipant(member.id!!)?.id!!
        val newRole = GroupChatRole.MODERATOR
        groupChatService.updateParticipant(
            groupChat.id!!, admin.id!!,
            ParticipantUpdateDto(participantId, newRole)
        )

        // then
        val findParticipant = participantRepository.findByIdOrNull(participantId)
        assertThat(findParticipant?.role).isEqualTo(newRole)
    }

    @Test
    @DisplayName("존재하지 않는 참여자의 정보 수정 시도시 RuntimeException 발생")
    fun updateParticipantNotExists(){
        //  given
        val user = userRepository.save(generateUser())
        val groupChat = groupChatRepository.save(generateGroupChat(user))
        val dto = ParticipantUpdateDto(user.id!!+1, GroupChatRole.MODERATOR)

        // expected
        assertThatThrownBy {
            groupChatService.updateParticipant(groupChat.id!!,user.id!!,dto)
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @DisplayName("참여자 수정 권한이 없는데 수정 시도시 RuntimeException 예외가 발생한다.")
    fun updateParticipantNoAuthority() {
        // given
        val admin = userRepository.save(generateUser())
        val members = userRepository.saveAll((1..2).map { generateUser() })
        val groupChat = generateGroupChat(admin)
        members.map { groupChat.join(it) }
        groupChatRepository.save(groupChat)

        // when
        val modifier = members[0]
        val target = members[1]
        val participantId = groupChat.getParticipant(target.id!!)?.id!!
        val newRole = GroupChatRole.MODERATOR

        assertThatThrownBy {
            groupChatService.updateParticipant(
                groupChat.id!!, modifier.id!!,
                ParticipantUpdateDto(participantId, newRole)
            )
        }.isInstanceOf(RuntimeException::class.java)
    }
}
