package sj.messenger.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.chat.domain.ChatRoom

interface ChatRoomRepository : JpaRepository<ChatRoom, Long?> {

    @Query("select c from ChatRoom c join fetch c.participants where c.id = :id")
    fun findWithParticipantsById(id : Long) : ChatRoom?
}