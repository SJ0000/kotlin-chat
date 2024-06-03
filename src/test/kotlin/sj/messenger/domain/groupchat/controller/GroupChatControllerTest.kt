package sj.messenger.domain.groupchat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import sj.messenger.domain.groupchat.domain.GroupChatRole
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.dto.GroupChatDto
import sj.messenger.domain.groupchat.dto.InvitationDto
import sj.messenger.domain.groupchat.dto.ServerGroupMessageDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.GroupMessageRepository
import sj.messenger.domain.groupchat.repository.InvitationRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.*
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.integration.IntegrationTest

@IntegrationTest
class GroupChatControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val om: ObjectMapper,
    @Autowired val groupChatRepository: GroupChatRepository,
    @Autowired val invitationRepository: InvitationRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val groupMessageRepository: GroupMessageRepository,
) {

    @Test
    fun getGroupChatInfo() {
        // given
        val user = userRepository.save(generateUser())
        val groupChat = generateGroupChat(user)
        groupChatRepository.save(groupChat)

        // expected
        mockMvc.get("/chats/groups/${groupChat.id!!}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id") { value(groupChat.id) }
                jsonPath("name") { value(groupChat.name) }
                jsonPath("avatarUrl") { value(groupChat.avatarUrl) }
                jsonPath("participants[0].user.id") { value(user.id) }
                jsonPath("participants[0].user.name") { value(user.name) }
                jsonPath("participants[0].user.email") { value(user.email) }
            }
        }
    }

    @Test
    @InjectAccessToken
    fun postGroupChat() {
        // given
        val dto: GroupChatCreateDto = fixture.giveMeOne()

        // expected
        val result = mockMvc.post("/chats/groups") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(dto)
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("id") { isNumber() }
                jsonPath("name") { value(dto.name) }
                jsonPath("avatarUrl") { isString() }
            }
        }.andReturn()

        val groupChatDto = om.readValue<GroupChatDto>(result.response.contentAsString)
        assertThat(groupChatRepository.existsById(groupChatDto.id)).isTrue()
    }

    @Test
    @InjectAccessToken
    @DisplayName("POST /chats/groups : name이 255자 초과시 400 Bad Request")
    fun postGroupChatLengthOver() {
        // given
        val dto = GroupChatCreateDto(randomString(256));

        // expected
        mockMvc.post("/chats/groups") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(dto)
        }.andExpect {
            status { isBadRequest() }
            content {
                jsonPath("$.fieldErrors.name") { exists() }
            }
        }
    }

    @Test
    @InjectAccessToken
    fun getMyGroupChats() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val groupChat = generateGroupChat(user)
        groupChatRepository.save(groupChat)

        // expected
        mockMvc.get("/chats/groups/me") {
            accept = MediaType.APPLICATION_JSON
        }.andDo {
            print()
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$[0].id") { value(groupChat.id) }
                jsonPath("$[0].name") { value(groupChat.name) }
                jsonPath("$[0].avatarUrl") { value(groupChat.avatarUrl) }
            }
        }
    }

    @Test
    @InjectAccessToken
    fun joinGroupChat() {
        // given
        val groupChatCreator = userRepository.save(generateUser())
        val groupChat = generateGroupChat(groupChatCreator)
        groupChatRepository.save(groupChat)

        val user = userRepository.findByEmail("test@test.com")!!

        // expected
        mockMvc.post("/chats/groups/${groupChat.id!!}/join") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("id") { value(groupChat.id) }
                jsonPath("name") { value(groupChat.name) }
                jsonPath("avatarUrl") { value(groupChat.avatarUrl) }
                jsonPath("participants[*].user.email") { value(hasItem(user.email)) }
                jsonPath("participants[*].role") { value(hasItems(GroupChatRole.ADMIN.toString(), GroupChatRole.MEMBER.toString())) }
            }
        }

        val findGroupChat = groupChatRepository.findWithParticipantsById(groupChat.id!!)
        assertThat(findGroupChat?.isParticipant(user.id!!)).isTrue()
    }

    @Test
    @InjectAccessToken
    fun postInviteGroupChat() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val groupChat = generateGroupChat(user)
        groupChatRepository.save(groupChat)

        // expected
        val result = mockMvc.post("/chats/groups/${groupChat.id!!}/invites") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id") { isString() }
                jsonPath("groupChatId") { value(groupChat.id) }
                jsonPath("groupChatName") { value(groupChat.name) }
                jsonPath("inviterName") { value(user.name) }
                jsonPath("expiredAt") { exists() }
            }
        }.andReturn()

        val invitationDto = om.readValue<InvitationDto>(result.response.contentAsString)
        assertThat(invitationRepository.findById(invitationDto.id)).isNotNull
    }

    @Test
    @InjectAccessToken
    fun getInvitation() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val groupChat = groupChatRepository.save(generateGroupChat(user))
        val invitation: Invitation = generateInvitation(groupChat)
        invitationRepository.save(invitation)

        // expected
        mockMvc.get("/chats/groups/invites/${invitation.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id") { value(invitation.id) }
                jsonPath("groupChatId") { value(invitation.groupChatId) }
                jsonPath("groupChatName") { value(groupChat.name) }
                jsonPath("inviterName") { value(invitation.inviterName) }
                jsonPath("expiredAt") { exists() }
            }
        }
    }

    @Test
    @InjectAccessToken
    fun getDirectMessages() {
        // given
        val groupChatId = 1L
        groupMessageRepository.saveAll((1..20).map { generateGroupMessage(groupChatId) })

        // expected
        val result = mockMvc.get("/chats/groups/${groupChatId}/messages") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$") { isArray() }
                jsonPath("$.size()") { value(10) }
            }
        }.andReturn()

        val jsonString = result.response.contentAsString
        val messages = om.readValue<List<ServerGroupMessageDto>>(jsonString)
        assertThat(messages).isSortedAccordingTo { o1, o2 ->
            o1.receivedAt.compareTo(o2.receivedAt)
        }
    }
}