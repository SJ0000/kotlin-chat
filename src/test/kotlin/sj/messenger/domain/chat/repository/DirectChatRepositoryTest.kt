package sj.messenger.domain.chat.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import sj.messenger.RepositoryTest
import sj.messenger.domain.directchat.repository.DirectChatRepository
import sj.messenger.domain.user.repository.UserRepository

@RepositoryTest
class DirectChatRepositoryTest (
    @Autowired private val directChatRepository: DirectChatRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val em: TestEntityManager,
){


}