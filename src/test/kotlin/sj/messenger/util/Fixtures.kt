package sj.messenger.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import net.jqwik.api.Arbitraries
import net.jqwik.web.api.Web
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.user.domain.User

val fixture: FixtureMonkey = FixtureMonkey.builder()
    .plugin(KotlinPlugin())
    .plugin(JakartaValidationPlugin())
    .build()

fun generateGroupChat(): GroupChat {
    return GroupChat(name = fixture.giveMeOne(), avatarUrl = randomUrl())
}

fun generateUser(): User {
    return generateUser(null)
}

fun generateUser(id: Long?): User {
    return User(
        name = randomString(10,255),
        email = randomEmail(),
        password = randomPassword(),
        avatarUrl = randomUrl(),
        statusMessage = randomString(10,255),
        id = id,
        publicIdentifier = randomString(10,255)
    )
}

fun generateInvitation(groupChat: GroupChat): Invitation {
    return Invitation(
        id = Arbitraries.strings().alpha().numeric().ofLength(8).sample(),
        groupChatId = groupChat.id!!,
        inviterId = fixture.giveMeOne(),
        inviterName = fixture.giveMeOne()
    )
}

fun randomUrl() = Web.webDomains().sample()

fun randomEmail(): String {
    val id = Arbitraries.strings().numeric().alpha().ofMinLength(5).ofMaxLength(15).sample()
    val domain = Arbitraries.strings().numeric().alpha().ofMinLength(5).ofMaxLength(15).sample()
    return "${id}@${domain}.com".lowercase()
}

fun randomPassword() = Arbitraries.strings().ascii().numeric().ofMinLength(10).ofMaxLength(20).sample()

fun randomString(minLength: Int, maxLength: Int) = Arbitraries.strings().ofMinLength(minLength).ofMaxLength(maxLength).sample()