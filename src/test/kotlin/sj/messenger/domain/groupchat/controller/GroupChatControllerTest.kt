package sj.messenger.domain.groupchat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import sj.messenger.domain.groupchat.repository.GroupChatRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.config.WithMockAccessToken
import sj.messenger.util.generateGroupChat
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
    @Autowired val userRepository: UserRepository,
){

    @Test
    @WithMockAccessToken
    fun getChatRoomInfo(){
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
}


