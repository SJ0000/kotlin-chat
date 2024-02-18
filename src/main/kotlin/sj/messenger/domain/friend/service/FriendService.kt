package sj.messenger.domain.friend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.user.domain.User
import sj.messenger.domain.user.service.UserService

@Service
@Transactional(readOnly = true)
class FriendService (
    private val userService: UserService,
    private val friendRepository: FriendRepository,
){
    fun getFriends(userId: Long) : List<User>{
        val friends = friendRepository.findApprovedAll(userId)
        // 조회할 user id 추출
        val friendUserIds = extractFriendsUserId(userId, friends)
        return userService.findUsers(friendUserIds)
    }

    private fun extractFriendsUserId(myId: Long, friends: List<Friend>) : List<Long>{
        val ids = mutableSetOf<Long>()
        friends.forEach{
            ids.add(it.fromUser.id!!)
            ids.add(it.toUser.id!!)
        }
        ids.remove(myId)
        return ids.toList()
    }


    fun getReceivedRequests(receiverId: Long) : List<Friend>{
        return friendRepository.findAllByToUserIdWithFromUser(receiverId, FriendStatus.PENDING)
    }

    @Transactional
    fun request(requesterId: Long, recipientPublicIdentifier: String){
        // 이미 친구인 경우
        val toUser = userService.findUserByPublicIdentifier(recipientPublicIdentifier)
        val toUserId = toUser.id!!

        val alreadyFriends = friendRepository.existsIgnoreFromTo(requesterId, toUserId, FriendStatus.APPROVED)
        if(alreadyFriends)
            throw RuntimeException("Friends Request failed. Already friends.")

        // 내가 보낸 요청이 있을 경우
        friendRepository.findByFromTo(requesterId, toUserId, FriendStatus.PENDING)?.also{
            throw RuntimeException("Friends Request failed. Request Already exists.")
        }

        // 거절된 요청이 있는 경우
        val rejected = friendRepository.findByFromTo(requesterId, toUserId, FriendStatus.REJECTED)
        if(rejected != null){
            rejected.reRequest()
            return
        }

        // 내가 보낸 요청이 없으면 생성
        val fromUser = userService.findUserById(requesterId)

        friendRepository.save(Friend(fromUser = fromUser, toUser = toUser))
    }

    @Transactional
    fun approveRequest(userId : Long, friendId: Long){
        val friend = friendRepository.findByIdOrNull(friendId) ?: throw RuntimeException("Friend not exists. id = ${friendId}")
        // 요청을 받은 사람 인가?
        if(!friend.isReceiver(userId))
            throw RuntimeException("User(Id = ${userId} ) is not receiver. receiver id = ${friend.toUser.id}")

        friend.approve()
    }
}