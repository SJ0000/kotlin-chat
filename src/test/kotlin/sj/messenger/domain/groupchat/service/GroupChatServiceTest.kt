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
import sj.messenger.util.annotation.ServiceTest
import sj.messenger.util.fixture
import sj.messenger.util.generateGroupChat
import sj.messenger.util.generateUser
import sj.messenger.util.randomString

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
        val user = generateUser()
        userRepository.save(user)
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
        val user = generateUser()
        userRepository.save(user)
        val groupChatCreateDto = fixture.giveMeOne<GroupChatCreateDto>()

        // when
        val chatRoomId = groupChatService.createGroupChat(user.id!!, groupChatCreateDto)

        // then
        val findChatRoom = groupChatRepository.findByIdOrNull(chatRoomId)
        assertThat(findChatRoom).isNotNull
        assertThat(findChatRoom?.name).isEqualTo(groupChatCreateDto.name)
    }

    @Test
    @DisplayName("특정 사용자의 참가한 대화방 리스트 조회")
    fun findUserChatRooms() {
        // given
        val user = generateUser()
        userRepository.save(user)
        val chatRooms = (1..3).map { generateGroupChat(user) }
        groupChatRepository.saveAll(chatRooms)

        // when
        val userChatRooms = groupChatService.findUserGroupChats(user.id!!)

        // then
        assertThat(userChatRooms.size).isEqualTo(chatRooms.size)
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
    @DisplayName("참여자 수정 권한이 없는데 수정 시도시 RuntimeException 예외가 발생한다.")
    fun updateParticipantError() {
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
