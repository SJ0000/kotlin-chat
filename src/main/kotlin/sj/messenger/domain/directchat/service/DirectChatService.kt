package sj.messenger.domain.directchat.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.directchat.domain.DirectChat
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.user.service.UserService
import sj.messenger.global.exception.AlreadyExistsException
import sj.messenger.global.exception.EntityNotFoundException
import sj.messenger.global.exception.PermissionDeniedException

@Service
@Transactional(readOnly = true)
class DirectChatService(
    private val userService: UserService,
    private val directChatRepository: DirectChatRepository,
) {
    fun getDirectChat(userId: Long, id: Long): DirectChat {
        val directChat =
            directChatRepository.findByIdWithUsers(id) ?: throw EntityNotFoundException(DirectChat::class, "id", id)
        if (!directChat.hasAuthority(userId))
            throw PermissionDeniedException(DirectChat::class, id, userId)
        return directChat
    }

    fun getUserDirectChats(userId: Long): List<DirectChat> {
        return directChatRepository.findAllByUserIdWithUsers(userId)
    }

    @Transactional
    fun createDirectChat(userIds: Pair<Long, Long>): Long {
        validateAlreadyExists(userIds)
        val users = userService.findUsers(setOf(userIds.first, userIds.second))
        val directChat = directChatRepository.save(DirectChat(users[0], users[1]))
        return directChat.id!!
    }

    private fun validateAlreadyExists(userIds: Pair<Long, Long>) {
        if (directChatRepository.existsByUserIds(userIds))
            throw AlreadyExistsException(DirectChat::class, "userIds", userIds)
    }
}