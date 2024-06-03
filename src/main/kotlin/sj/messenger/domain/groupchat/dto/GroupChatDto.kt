package sj.messenger.domain.groupchat.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import sj.messenger.domain.groupchat.domain.GroupChatRole
import sj.messenger.domain.user.dto.UserDto

data class GroupChatDto(
    val id: Long,
    val name: String,
    val avatarUrl: String,
    val participants: List<ParticipantDto> = listOf()
)

data class ParticipantDto(
    val user: UserDto,
    val role: GroupChatRole
)


data class GroupChatCreateDto(
    @field:NotEmpty
    @field:Size(max = 255)
    val name: String
)
