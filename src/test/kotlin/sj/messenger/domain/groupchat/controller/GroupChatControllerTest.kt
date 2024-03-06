package sj.messenger.domain.groupchat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.groupchat.dto.GroupChatCreateDto
import sj.messenger.domain.groupchat.dto.GroupChatDto
import sj.messenger.domain.groupchat.dto.InvitationDto
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.groupchat.repository.InvitationRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.config.WithMockAccessToken
import sj.messenger.util.fixture
import sj.messenger.util.generateGroupChat
import sj.messenger.util.generateInvitation
import sj.messenger.util.generateUser
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.integration.EnableMockAuthentication

@SpringBootTest
@EnableContainers
@EnableMockAuthentication
@AutoConfigureMockMvc
class GroupChatControllerTest(
    @Autowired val mockMvc : MockMvc,
    @Autowired val om : ObjectMapper,
    @Autowired val groupChatRepository: GroupChatRepository,
    @Autowired val invitationRepository: InvitationRepository,
    @Autowired val userRepository: UserRepository,
){

    @Test
    fun getGroupChatInfo(){
        // given
        val user = userRepository.save(generateUser())
        val groupChat = generateGroupChat()
        groupChat.join(user)
        groupChatRepository.save(groupChat)

        // expected
        mockMvc.get("/chats/groups/${groupChat.id!!}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id",groupChat.id!!)
                jsonPath("name",groupChat.name)
                jsonPath("avatarUrl",groupChat.avatarUrl)
                jsonPath("users[0].id", user.id!!)
                jsonPath("users[0].name", user.name)
                jsonPath("users[0].email", user.email)
            }
        }
    }

    @Test
    @WithMockAccessToken
    fun postGroupChat(){
        // given
        val dto : GroupChatCreateDto = fixture.giveMeOne()

        // expected
        val result = mockMvc.post("/chats/groups") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(dto)
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("id").isNumber
                jsonPath("name",dto.name)
                jsonPath("avatarUrl").isString
            }
        }.andReturn()

        val groupChatDto = om.readValue<GroupChatDto>(result.response.contentAsString)
        assertThat(groupChatRepository.existsById(groupChatDto.id)).isTrue()
    }

    @Test
    @WithMockAccessToken
    fun getMyGroupChats(){
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val groupChat = generateGroupChat()
        groupChat.join(user)
        groupChatRepository.save(generateGroupChat())

        // expected
        mockMvc.get("/chats/groups/me") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$[0].id",groupChat.id)
                jsonPath("$[0].name",groupChat.name)
                jsonPath("$[0].avatarUrl",groupChat.avatarUrl)
            }
        }
    }

    @Test
    @WithMockAccessToken
    fun joinGroupChat(){
        // given
        val groupChat = generateGroupChat()
        groupChatRepository.save(groupChat)

        // expected
        mockMvc.post("/chats/groups/${groupChat.id!!}/join") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("id",groupChat.id)
                jsonPath("name",groupChat.name)
                jsonPath("avatarUrl",groupChat.avatarUrl)
                jsonPath("users[0].email","test@test.com")
            }
        }

        val user = userRepository.findAll()[0]
        val findGroupChat = groupChatRepository.findWithParticipantsById(groupChat.id!!)
        assertThat(findGroupChat?.isParticipant(user.id!!)).isTrue()
    }

    @Test
    @WithMockAccessToken
    fun postInviteGroupChat(){
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val groupChat = generateGroupChat()
        groupChat.join(user)
        groupChatRepository.save(groupChat)

        // expected
        val result = mockMvc.post("/chats/groups/${groupChat.id!!}/invites") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id").isString
                jsonPath("groupChatId", groupChat.id)
                jsonPath("groupChatName", groupChat.avatarUrl)
                jsonPath("inviterName", user.name)
                jsonPath("expiredAt").exists()
            }
        }.andReturn()

        val invitationDto = om.readValue<InvitationDto>(result.response.contentAsString)
        assertThat(invitationRepository.findById(invitationDto.id)).isNotNull
    }

    @Test
    fun getInvitation(){
        // given
        val groupChat = groupChatRepository.save(generateGroupChat())
        val invitation : Invitation = generateInvitation(groupChat)
        invitationRepository.save(invitation)

        // expected
        mockMvc.get("/chats/groups/invites/${invitation.id}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id",invitation.id)
                jsonPath("groupChatId", invitation.groupChatId)
                jsonPath("groupChatName", groupChat.name)
                jsonPath("inviterName", invitation.inviterName)
                jsonPath("expiredAt").exists()
            }
        }
    }
}