package sj.messenger.domain.directchat.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import sj.messenger.util.annotation.MongoRepositoryTest
import sj.messenger.util.generateDirectMessage
import sj.messenger.util.truncateMicroSeconds
import java.time.LocalDateTime


@MongoRepositoryTest
class DirectMessageRepositoryTest (
    @Autowired val directMessageRepository: DirectMessageRepository,
){

    @BeforeEach
    fun beforeEach(){
        directMessageRepository.deleteAll()
    }

    @Test
    @DisplayName("특정 DirectChat의 최신 메시지를 일정 갯수 조회한다.")
    fun findPreviousMessages(){
        // given
        val directChatId = 1L
        val now = LocalDateTime.now()
        val pastMessages = (1..11).map {
            generateDirectMessage(directChatId, maxSentAt = now)
        }
        val futureMessages = (1..10).map {
            generateDirectMessage(
                directChatId = directChatId,
                minSentAt = now.plusSeconds(1),
                maxSentAt = now.plusYears(1))
        }
        directMessageRepository.saveAll(pastMessages)
        directMessageRepository.saveAll(futureMessages)

        // when
        val pageRequest = PageRequest.of(0, 10)
        val recentMessages =
            directMessageRepository.findPreviousMessages(directChatId, LocalDateTime.now(), pageRequest)

        // then
        assertThat(recentMessages.size).isEqualTo(pageRequest.pageSize)
        recentMessages.map{
            assertThat(it.sentAt).isBeforeOrEqualTo(now)
        }

        val expected = pastMessages.sortedBy { it.sentAt }.subList(1,11).reversed()

        expected.zip(recentMessages)
            .forEach{
                assertThat(it.first.senderId).isEqualTo(it.second.senderId)
                assertThat(truncateMicroSeconds(it.first.sentAt)).isEqualTo(it.second.sentAt)
                assertThat(it.first.content).isEqualTo(it.second.content)
                assertThat(it.first.directChatId).isEqualTo(it.second.directChatId)
            }
    }
}