package sj.messenger.domain.directchat.controller

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.config.InjectAccessToken
import sj.messenger.util.generateUser
import sj.messenger.util.integration.IntegrationTest

@IntegrationTest
class DirectChatControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userRepository: UserRepository,
    @Autowired val directChatRepository: DirectChatRepository,
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
                jsonPath("id", directChat.id)
                jsonPath("otherUser.id", other.id)
                jsonPath("otherUser.name", other.name)
                jsonPath("otherUser.email", other.email)
                jsonPath("otherUser.avatarUrl", other.avatarUrl)
                jsonPath("otherUser.statusMessage", other.statusMessage)
                jsonPath("otherUser.publicIdentifier", other.publicIdentifier)
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
                jsonPath("$").isNumber
            }
        }.andReturn()

        val directChatId = result.response.contentAsString.toLong()
        val directChat = directChatRepository.findByIdWithUsers(directChatId)!!
        assertThat(directChat.getOtherUser(user.id!!).id).isEqualTo(other.id)
    }
}