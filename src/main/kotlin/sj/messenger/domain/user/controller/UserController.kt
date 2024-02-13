package sj.messenger.domain.user.controller

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
        val user = userService.findUser(id);
        val userDto = UserDto(id = id, name = user.name, email = user.email)
        return ResponseEntity.ok(userDto)
    }

    @PostMapping("/signup")
    fun signUp(@RequestBody signUp: SignUpDto): ResponseEntity<UserDto> {
        val userId = userService.signUpUser(signUp)
        val user = userService.findUser(userId)
        val userDto = UserDto(id = userId, name = user.name, email = user.email)
        return ResponseEntity.created(URI.create("/users/${userId}"))
            .body(userDto)
    }

    @PatchMapping("/users/{id}")
    fun patchUser(
        @AuthenticationPrincipal userDetails: LoginUserDetails,
        @PathVariable id: Long,
        @RequestBody dto: UpdateUserDto
    ): ResponseEntity<UserDto> {
        if (userDetails.getUserId() != id)
            throw RuntimeException("has no permission. login user id = ${userDetails.getUserId()}, change request user id = ${id}")
        userService.updateUser(id, dto)
        val user = userService.findUser(id)
        val userDto = UserDto(id = id, name = user.name, email = user.email, avatarUrl = user.avatarUrl)
        return ResponseEntity.ok(userDto);
    }
}