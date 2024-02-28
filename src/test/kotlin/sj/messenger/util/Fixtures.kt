package sj.messenger.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import net.jqwik.api.Arbitraries
import net.jqwik.web.api.Web
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.user.domain.User

val fixture: FixtureMonkey = FixtureMonkey.builder()
    .plugin(KotlinPlugin())
    .plugin(JakartaValidationPlugin())
    .build()

fun generateChatRoom() : ChatRoom {
    return ChatRoom(name = fixture.giveMeOne(), avatarUrl = randomUrl())
}

fun generateUser() : User {
    return User(
        name = fixture.giveMeOne(),
        email = Web.emails().fixGenSize(40).sample(),
        password = randomPassword(),
        avatarUrl = randomUrl(),
        statusMessage = fixture.giveMeOne(),
        id = Arbitraries.longs().greaterOrEqual(1).sample(),
        publicIdentifier = fixture.giveMeOne()
    )
}

fun randomUrl() = Web.webDomains().sample()

fun randomEmail() = Web.emails().fixGenSize(40).sample()

fun randomPassword() = Arbitraries.strings().ascii().numeric().ofMinLength(10).ofMaxLength(20).sample()
