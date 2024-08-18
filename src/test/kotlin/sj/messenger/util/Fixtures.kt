package sj.messenger.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import net.jqwik.api.Arbitraries
import net.jqwik.time.api.DateTimes
import net.jqwik.web.api.Web
import sj.messenger.domain.directchat.domain.DirectMessage
import sj.messenger.domain.groupchat.domain.GroupChat
import sj.messenger.domain.groupchat.domain.GroupMessage
import sj.messenger.domain.groupchat.domain.Invitation
import sj.messenger.domain.user.domain.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val fixture: FixtureMonkey = FixtureMonkey.builder()
    .plugin(KotlinPlugin())
    .plugin(JakartaValidationPlugin())
    .build()

fun generateGroupChat(user: User): GroupChat {
    return GroupChat.create(creator = user, name = randomString(1, 255))
}

fun generateUser(): User {
    return generateUser(null)
}

fun generateUser(id: Long?): User {
    return User(
        name = randomString(10, 255),
        email = randomEmail(),
        password = randomPassword(),
        avatarUrl = randomUrl(),
        statusMessage = randomString(10, 255),
        id = id,
        publicIdentifier = randomString(10, 255)
    )
}

fun generateDirectMessage(
    directChatId: Long,
    minSentAt: LocalDateTime = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN),
    maxSentAt: LocalDateTime = LocalDateTime.now()
): DirectMessage {
    return DirectMessage(
        senderId = fixture.giveMeOne(),
        directChatId = directChatId,
        content = randomString(1, 200),
        sentAt = randomDateTime(minSentAt, maxSentAt),
    )
}

fun generateGroupMessage(
    groupChatId: Long,
    minSentAt: LocalDateTime = LocalDateTime.of(LocalDate.EPOCH, LocalTime.MIN),
    maxSentAt: LocalDateTime = LocalDateTime.now()
): GroupMessage {
    return GroupMessage(
        senderId = fixture.giveMeOne(),
        groupChatId = groupChatId,
        content = randomString(1, 200),
        sentAt = randomDateTime(minSentAt, maxSentAt)
    )
}

fun generateInvitation(groupChat: GroupChat): Invitation {
    return Invitation(
        id = Arbitraries.strings().alpha().numeric().ofLength(8).sample(),
        groupChatId = groupChat.id!!,
        inviterId = fixture.giveMeOne(),
        inviterName = randomString(1, 255)
    )
}

fun randomUrl() = Web.webDomains().sample()

fun randomEmail(): String {
    val id = Arbitraries.strings().numeric().alpha().ofMinLength(5).ofMaxLength(15).sample()
    val domain = Arbitraries.strings().numeric().alpha().ofMinLength(5).ofMaxLength(15).sample()
    return "${id}@${domain}.com".lowercase()
}

fun randomPassword() = Arbitraries.strings().ascii().numeric().ofMinLength(10).ofMaxLength(20).sample()

fun randomString(minLength: Int, maxLength: Int) =
    Arbitraries.strings().ofMinLength(minLength).ofMaxLength(maxLength).sample()

fun randomString(length: Int) = randomString(length, length)

fun randomAlphabets(length: Int) = Arbitraries.strings().alpha().ofLength(length).sample()

fun randomDateTime(min: LocalDateTime, max: LocalDateTime) = DateTimes.dateTimes().between(min, max).sample()

fun randomLong(positive: Boolean = true) : Long{
    val min = if (positive) 0 else Long.MIN_VALUE
    return Arbitraries.longs().between(min,Long.MAX_VALUE).sample()
}