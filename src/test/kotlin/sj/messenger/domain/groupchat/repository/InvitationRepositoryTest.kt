package sj.messenger.domain.groupchat.repository

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.util.annotation.RedisRepositoryTest
import sj.messenger.util.fixture

@RedisRepositoryTest
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