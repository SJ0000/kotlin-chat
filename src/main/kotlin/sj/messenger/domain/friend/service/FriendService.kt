package sj.messenger.domain.friend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendRequest
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.friend.repository.FriendRequestRepository
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.service.UserService
import sj.messenger.global.exception.AlreadyExistsException
import sj.messenger.global.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class FriendService(
    private val userService: UserService,
    private val friendRepository: FriendRepository,
    private val friendRequestRepository: FriendRequestRepository,
) {
    fun getFriends(userId: Long): List<User> {
        val friends = friendRepository.findAll(userId)
        val friendUserIds = extractFriendsUserId(userId, friends)
        return userService.findUsers(friendUserIds.toSet())
    }

    fun getReceivedRequests(receiverId: Long): List<FriendRequest> {
        return friendRequestRepository.findReceivedAllWithSender(receiverId)
    }

    @Transactional
    fun request(senderId: Long, receiverPublicIdentifier: String) {
        // 이미 친구인 경우
        val receiver = userService.findUserByPublicIdentifier(receiverPublicIdentifier)
        val receiverId = receiver.id!!

        val alreadyFriends = friendRepository.exists(senderId, receiverId)
        if (alreadyFriends)
            throw AlreadyExistsException(Friend::class, "senderId = ${senderId}, receiverId = ${receiverId}")

        // 내가 보낸 요청이 있을 경우
        friendRequestRepository.findByFromTo(senderId, receiverId)?.also {
            throw AlreadyExistsException(FriendRequest::class, "senderId = ${senderId}, receiverId = ${receiverId}")
        }

        // 내가 보낸 요청이 없으면 생성
        val fromUser = userService.findUserById(senderId)
        friendRequestRepository.save(FriendRequest(sender = fromUser, receiver = receiver))
    }

    @Transactional
    fun approveRequest(userId: Long, requestId: Long) {
        val request = friendRequestRepository.findByIdWithUsers(requestId) ?: throw EntityNotFoundException(
            FriendRequest::class,
            "id",
            requestId
        )
        request.approveIfPossible(userId)
        friendRepository.save(Friend(request.sender, request.receiver))
    }

    private fun extractFriendsUserId(myId: Long, friends: List<Friend>): List<Long> {
        return friends.map { it.user1.id!! }
            .union(friends.map { it.user2.id!! })
            .filterNot { it == myId }
            .toList();
    }
}