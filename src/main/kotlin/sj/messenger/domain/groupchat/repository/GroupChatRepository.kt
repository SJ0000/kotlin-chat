package sj.messenger.domain.groupchat.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.groupchat.domain.GroupChat

interface GroupChatRepository : JpaRepository<GroupChat, Long?> {

    @Query("select c from GroupChat c left join fetch c.participants where c.id = :id")
    fun findWithParticipantsById(id : Long) : GroupChat?
}