package sj.messenger.domain.chat.repository

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.test.context.ContextConfiguration
import sj.messenger.domain.chat.domain.Invitation
import sj.messenger.util.fixture
import sj.messenger.util.repository.RedisContainerInitializer

@DataRedisTest
@ContextConfiguration(initializers = [RedisContainerInitializer::class])
class InvitationRepositoryTest (
    @Autowired val invitationRepository: InvitationRepository,
){

    @Test
    fun add(){
        invitationRepository.save(Invitation(
            id = fixture.giveMeOne(),
            chatRoomId = fixture.giveMeOne(),
            inviterId = fixture.giveMeOne(),
            inviterName = fixture.giveMeOne()
        ))
    }
}