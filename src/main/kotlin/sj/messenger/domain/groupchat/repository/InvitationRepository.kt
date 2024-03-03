package sj.messenger.domain.groupchat.repository

import org.springframework.data.keyvalue.repository.KeyValueRepository
import sj.messenger.domain.groupchat.domain.Invitation

interface InvitationRepository : KeyValueRepository<Invitation, String> {

}