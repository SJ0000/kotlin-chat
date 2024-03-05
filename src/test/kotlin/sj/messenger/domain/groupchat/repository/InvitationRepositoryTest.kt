package sj.messenger.domain.groupchat.repository

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.util.fixture
import sj.messenger.util.testcontainer.annotation.EnableRedisContainer

@DataRedisTest
@EnableRedisContainer
class InvitationRepositoryTest (
    @Autowired val invitationRepository: InvitationRepository,
){

    @Test
    fun add(){
        invitationRepository.save(Invitation(
            id = fixture.giveMeOne(),
            groupChatId = fixture.giveMeOne(),
            inviterId = fixture.giveMeOne(),
            inviterName = fixture.giveMeOne()
        ))
    }
}