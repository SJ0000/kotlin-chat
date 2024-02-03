package sj.messenger.domain.chat.repository

import org.springframework.data.repository.CrudRepository
import sj.messenger.domain.chat.domain.Invitation

interface InvitationRepository : CrudRepository<Invitation, String> {
}