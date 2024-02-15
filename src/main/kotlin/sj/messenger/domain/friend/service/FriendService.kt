package sj.messenger.domain.friend.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendStatus
import sj.messenger.domain.friend.repository.FriendRepository
import sj.messenger.domain.user.service.UserService

@Service
@Transactional(readOnly = true)
class FriendService (
    private val userService: UserService,
    private val friendRepository: FriendRepository,
){

    @Transactional
    fun request(fromUserId: Long, toUserId: Long){
        // 이미 친구인 경우
        val alreadyFriends = friendRepository.existsIgnoreFromTo(fromUserId, toUserId, FriendStatus.APPROVED)
        if(alreadyFriends)
            throw RuntimeException("Friends Request failed. Already friends.")

        // 내가 보낸 요청이 있을 경우
        friendRepository.findByFromTo(fromUserId, toUserId, FriendStatus.PENDING)?.also{
            throw RuntimeException("Friends Request failed. Request Already exists.")
        }

        // 거절된 요청이 있는 경우
        val rejected = friendRepository.findByFromTo(fromUserId, toUserId, FriendStatus.REJECTED)
        if(rejected != null){
            rejected.reRequest()
            return
        }

        // 내가 보낸 요청이 없으면 생성
        val fromUser = userService.findUser(fromUserId)
        val toUser = userService.findUser(toUserId)
        friendRepository.save(Friend(fromUser = fromUser, toUser = toUser))
    }

    @Transactional
    fun approveRequest(userId : Long, friendId: Long){
        val friend = friendRepository.findByIdOrNull(friendId) ?: throw RuntimeException("Friend not exists. id = ${friendId}")
        // 요청을 받은 사람 인가?
        if(!friend.isReceiver(userId))
            throw RuntimeException("is not receiver. userId = ${userId} ,receiver id = ${friend.toUser.id}")

        friend.approve()
    }
}