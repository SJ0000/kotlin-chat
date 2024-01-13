package sj.messenger.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import sj.messenger.domain.chat.domain.ChatRoom

interface ChatRoomRepository : JpaRepository<ChatRoom, Long?> {
}