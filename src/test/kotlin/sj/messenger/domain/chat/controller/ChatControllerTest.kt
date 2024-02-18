package sj.messenger.domain.chat.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import sj.messenger.domain.chat.domain.ChatRoom
import sj.messenger.domain.chat.dto.ChatRoomCreate
import sj.messenger.domain.chat.repository.ChatRoomRepository
import sj.messenger.domain.chat.service.ChatService
import sj.messenger.util.generateChatRoom

@SpringBootTest
class ChatControllerTest(
){

}


