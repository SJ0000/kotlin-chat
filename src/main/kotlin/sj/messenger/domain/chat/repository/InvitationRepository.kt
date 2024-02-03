package sj.messenger.domain.chat.repository

import org.springframework.data.keyvalue.repository.KeyValueRepository
import sj.messenger.domain.chat.domain.Invitation

interface InvitationRepository : KeyValueRepository<Invitation, String> {
}