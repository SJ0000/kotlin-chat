package sj.messenger.domain.groupchat.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.groupchat.domain.Participant

interface ParticipantRepository : JpaRepository<Participant, Long?> {

    @Query("select p from Participant p join fetch p.groupChat where p.user.id = :userId")
    fun getParticipantsWithGroupChatByUserId(userId: Long) : List<Participant>

    @Query("select p from Participant p where p.groupChat.id = :groupChatId and p.user.id = :userId")
    fun findByGroupChatIdAndUserId(groupChatId: Long, userId: Long) : Participant
}