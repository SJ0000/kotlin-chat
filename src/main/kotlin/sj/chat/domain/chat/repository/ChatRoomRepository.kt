package sj.chat.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import sj.chat.domain.chat.domain.ChatRoom

interface ChatRoomRepository : JpaRepository<ChatRoom, Long?> {
}