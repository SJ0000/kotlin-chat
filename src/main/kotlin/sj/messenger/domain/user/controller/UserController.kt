package sj.messenger.domain.user.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.security.authentication.principal.LoginUserDetails
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.dto.UpdateUserDto
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService
import java.net.URI

@RestController
class UserController(
    val userService: UserService
) {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users/{id}")
    fun user(@PathVariable id: Long): ResponseEntity<UserDto> {
        val user = userService.findUserById(id);
        return ResponseEntity.ok(UserDto(user))
    }

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody signUp: SignUpDto): ResponseEntity<UserDto> {
        val userId = userService.signUpUser(signUp)
        val user = userService.findUserById(userId)
        return ResponseEntity.created(URI.create("/users/${userId}"))
            .body(UserDto(user))
    }

    @PatchMapping("/users/{id}")
    fun patchUser(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
        @Valid @RequestBody dto: UpdateUserDto
    ): ResponseEntity<UserDto> {
        if (userDetails.getUserId() != id)
            throw RuntimeException("has no permission. login user id = ${userDetails.getUserId()}, change request user id = ${id}")
        userService.updateUser(id, dto)
        val user = userService.findUserById(id)
        return ResponseEntity.ok(UserDto(user));
    }
}