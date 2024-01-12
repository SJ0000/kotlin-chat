package sj.chat.domain.user.dto

import kotlin.jvm.internal.Ref.LongRef

data class UserDto(
    val id : Long,
    val name : String,
    val email : String,
)
