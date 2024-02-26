package sj.messenger.domain.friend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.friend.domain.Friend
import sj.messenger.domain.friend.domain.FriendRequest

interface FriendRepository : JpaRepository<Friend,Long?>, FriendRepositoryCustom{


}