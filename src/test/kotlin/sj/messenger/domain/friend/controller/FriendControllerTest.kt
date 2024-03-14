package sj.messenger.domain.friend.controller


import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendRequest
import sj.messenger.domain.friend.dto.FriendRequestDto
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.friend.repository.FriendRequestRepository
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.config.WithMockAccessToken
import sj.messenger.util.generateUser
import sj.messenger.util.integration.EnableContainers
import sj.messenger.util.integration.EnableMockAuthentication


@SpringBootTest
@EnableContainers
@EnableMockAuthentication
@AutoConfigureMockMvc
class FriendControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val friendRepository: FriendRepository,
    @Autowired val friendRequestRepository: FriendRequestRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val om: ObjectMapper,
) {

    @BeforeEach
    fun clearTestData(){
        friendRequestRepository.deleteAll()
    }

    @Test
    @WithMockAccessToken
    fun getFriends() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val others = (1..3).map { generateUser() }
        userRepository.saveAll(others)
        friendRepository.saveAll(others.map { Friend(user, it) })

        // expected
        mockMvc.get("/friends") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$").isArray
                jsonPath("$.size()", 3)
                jsonPath(
                    "$[*].id", Matchers.containsInAnyOrder(
                        *others.map { it.id?.toInt() }.toTypedArray()
                    )
                )
            }
        }
    }

    @Test
    @WithMockAccessToken
    fun getFriendRequests() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val others = (1..3).map { generateUser() }
        userRepository.saveAll(others)
        friendRequestRepository.saveAll(others.map { FriendRequest(it, user) })

        // expected
        mockMvc.get("/friends/requests") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$").isArray
                jsonPath("$.size()", 2)
                jsonPath(
                    "$[*].fromUser.id", Matchers.containsInAnyOrder(
                        *others.map { it.id?.toInt() }.toTypedArray()
                    )
                )
            }
        }
    }

    @Test
    @WithMockAccessToken
    fun postFriends() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val other = userRepository.save(generateUser())
        val requestDto = FriendRequestDto(other.publicIdentifier)

        // expected
        mockMvc.post("/friends") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = om.writeValueAsString(requestDto)
        }.andExpect {
            status { isCreated() }
        }

        val receivedRequest = friendRequestRepository.findByFromTo(user.id!!, other.id!!)
        assertThat(receivedRequest).isNotNull
    }

    @Test
    @WithMockAccessToken
    fun patchFriends() {
        // given
        val user = userRepository.findByEmail("test@test.com")!!
        val other = userRepository.save(generateUser())
        val friendRequest = friendRequestRepository.save(FriendRequest(other, user))

        // expected
        mockMvc.patch("/friends/requests/${friendRequest.id!!}/approve") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        assertThat(friendRepository.exists(user.id!!, other.id!!)).isTrue()
    }
}