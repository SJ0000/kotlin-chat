package sj.messenger.domain.friend.repository

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import sj.messenger.RepositoryTest
import sj.messenger.domain.friend.domain.FriendRequest
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.util.generateUser


@RepositoryTest
class FriendRequestRepositoryTest(
    @Autowired private val friendRequestRepository: FriendRequestRepository,
    @Autowired private val userRepository: UserRepository,
) {

}