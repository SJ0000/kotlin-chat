package sj.messenger.domain.directchat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.dto.ServerDirectMessageDto
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.directchat.repository.DirectMessageRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.annotation.IntegrationTest
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.generateDirectMessage
import sj.messenger.util.generateUser

@IntegrationTest
class DirectChatControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userRepository: UserRepository,
    @Autowired val directChatRepository: DirectChatRepository,
    @Autowired val directMessageRepository: DirectMessageRepository,
    @Autowired val objectMapper: ObjectMapper,
) {

    @Test
    @InjectAccessToken
    fun getDirectChat() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val other = userRepository.save(generateUser())
        val directChat = directChatRepository.save(DirectChat(user, other))

        // expected
        mockMvc.get("/chats/directs/${directChat.id!!}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("id") { value(directChat.id) }
                jsonPath("otherUser.id") { value(other.id) }
                jsonPath("otherUser.name") { value(other.name) }
                jsonPath("otherUser.email") { value(other.email) }
                jsonPath("otherUser.avatarUrl") { value(other.avatarUrl) }
                jsonPath("otherUser.statusMessage") { value(other.statusMessage) }
                jsonPath("otherUser.publicIdentifier") { value(other.publicIdentifier) }
            }
        }
    }

    @Test
    @InjectAccessToken
    fun getMyDirectChats() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val others = userRepository.saveAll((1..3).map { generateUser() })
        val directChats = directChatRepository.saveAll(others.map {
            DirectChat(user, it)
        })

        // expected
        mockMvc.get("/chats/directs/me") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath(
                    "$[*].id", Matchers.containsInAnyOrder(
                        *directChats.map { it.id?.toInt() }.toTypedArray()
                    )
                )
            }
        }
    }

    @Test
    @InjectAccessToken
    fun postDirectChat() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val other = userRepository.save(generateUser())

        // expected
        val result = mockMvc.post("/chats/directs?to=${other.id!!}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("$") { isNumber() }
            }
        }.andReturn()

        val directChatId = result.response.contentAsString.toLong()
        val directChat = directChatRepository.findByIdWithUsers(directChatId)!!
        assertThat(directChat.getOtherUser(user.id!!).id).isEqualTo(other.id)
    }

    @Test
    @InjectAccessToken
    fun getDirectMessages() {
        // given
        val directChatId = 1L
        directMessageRepository.saveAll(
            (1..10).map { generateDirectMessage(directChatId) }
        )

        // expected
        val result = mockMvc.get("/chats/directs/${directChatId}/messages") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$") { isArray() }
                jsonPath("$.size()") { value(10) }
            }
        }.andReturn()

        val jsonString = result.response.contentAsString
        val messages = objectMapper.readValue<List<ServerDirectMessageDto>>(jsonString)
        assertThat(messages).isSortedAccordingTo { o1, o2 ->
            o1.receivedAt.compareTo(o2.receivedAt)
        }
    }
}