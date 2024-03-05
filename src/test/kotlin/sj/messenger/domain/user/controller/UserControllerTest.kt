package sj.messenger.domain.user.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.util.config.WithMockAccessToken
import sj.messenger.util.generateUser
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.integration.EnableMockAuthentication


@SpringBootTest
@EnableContainers
@EnableMockAuthentication
@AutoConfigureMockMvc
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userService: UserService,
    @Autowired val userRepository: UserRepository,
){

    @Test
    @WithMockAccessToken
    fun user(){
        // given
        val user = generateUser()
        userRepository.save(user)
        // expected
//        mockMvc.get("/users/${user.id!!}")
//            .andDo {
//                MockMvcResultHandlers.print()
//            }

        mockMvc.perform(get("/users/${user.id!!}"))
            .andDo(MockMvcResultHandlers.print())
//            .andExpect {
//                content {
//                    jsonPath("id", equalTo(user.id!!))
//                    jsonPath("name", equalTo(user.name))
//                    jsonPath("email", equalTo(user.email))
//                    jsonPath("avatarUrl", equalTo(user.avatarUrl))
//                    jsonPath("statusMessage", equalTo(user.statusMessage))
//                    jsonPath("publicIdentifier", equalTo(user.publicIdentifier))
//                }
//            }
    }
}