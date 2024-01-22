package sj.messenger.domain.chat.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sj.messenger.domain.chat.domain.Participant

interface ParticipantRepository : JpaRepository<Participant, Long?> {

    @Query("select p from Participant p join fetch p.chatRoom where p.user.id = :userId")
    fun getParticipantsByUserId(userId: Long) : List<Participant>
}