package sj.messenger.domain.groupchat.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import sj.messenger.util.annotation.MongoRepositoryTest
import sj.messenger.util.generateGroupMessage
import sj.messenger.util.truncateMicroSeconds
import java.time.LocalDateTime

@MongoRepositoryTest
class GroupMessageRepositoryTest (
    @Autowired val groupMessageRepository: GroupMessageRepository
){

    @BeforeEach
    fun beforeEach(){
        groupMessageRepository.deleteAll()
    }

    @Test
    fun findPreviousMessages(){
        // given
        val groupChatId = 1L
        val messages = (1..20).map {
            generateGroupMessage(groupChatId)
        }
        groupMessageRepository.saveAll(messages)

        // when
        val pageRequest = PageRequest.of(0, 5)
        val result = groupMessageRepository.findPreviousMessages(groupChatId, LocalDateTime.now(), pageRequest)

        // then
        val expected = messages.sortedBy { it.sentAt }.subList(15,20).reversed()

        result.zip(expected)
            .forEach{ (a, b) ->
                assertThat(a.senderId).isEqualTo(b.senderId)
                assertThat(a.groupChatId).isEqualTo(b.groupChatId)
                assertThat(a.content).isEqualTo(b.content)
                assertThat(a.sentAt).isEqualTo(truncateMicroSeconds(b.sentAt))
            }
    }
}