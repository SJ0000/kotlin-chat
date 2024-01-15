package sj.messenger.domain.user.controller

import org.springframework.context.annotation.Role
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import sj.messenger.domain.user.dto.SignUpDto
import sj.messenger.domain.user.dto.UserDto
import sj.messenger.domain.user.service.UserService
import java.net.URI

@RestController
class UserController(
    val userService: UserService
) {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users/{id}")
    fun user(@PathVariable id : Long) : ResponseEntity<UserDto>{
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
}