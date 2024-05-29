package sj.messenger.domain.user.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import sj.messenger.global.dto.serialize.StringLowercaseDeserializer

data class SignUpDto(
    @field:Email
    @field:Size(max = 255)
    @field:JsonDeserialize(using = StringLowercaseDeserializer::class)
    val email: String,
    @field:Size(max = 20)
    val name: String,
    @field:Size(min = 10, max = 20)
    val password: String,
)