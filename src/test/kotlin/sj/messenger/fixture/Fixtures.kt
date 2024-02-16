package sj.messenger.fixture

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import net.jqwik.api.Arbitraries
import net.jqwik.web.api.Web
import org.springframework.test.util.ReflectionTestUtils
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.user.domain.User
import sj.messenger.global.domain.BaseEntity

val fixture = FixtureMonkey.builder().plugin(KotlinPlugin()).build()

fun generateChatRoom() : ChatRoom {
    return ChatRoom(name = fixture.giveMeOne(), avatarUrl = fixture.giveMeOne())
}

fun generateUser() : User {
    return User(
        name = fixture.giveMeOne(),
        email = Web.emails().fixGenSize(40).sample(),
        password = fixture.giveMeOne(),
        avatarUrl = Web.webDomains().sample(),
        statusMessage = fixture.giveMeOne(),
        id = Arbitraries.longs().greaterOrEqual(1).sample(),
        publicIdentifier = fixture.giveMeOne()
    )
}
