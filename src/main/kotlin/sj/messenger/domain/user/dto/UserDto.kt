package sj.messenger.domain.user.dto

import sj.messenger.domain.user.domain.User

data class UserDto(
    val id : Long,
    val name : String,
    val email : String,
    val avatarUrl: String,
    val statusMessage: String,
    val publicIdentifier: String
){
    constructor(user : User) : this(
        id= user.id!!,
        name=user.name,
        email = user.email,
        avatarUrl = user.avatarUrl,
        statusMessage=user.statusMessage,
        publicIdentifier = user.publicIdentifier
    )
}
