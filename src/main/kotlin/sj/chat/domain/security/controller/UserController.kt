package sj.chat.domain.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sj.chat.domain.security.dto.SignUpDto
import sj.chat.domain.security.dto.UserDto
import sj.chat.domain.security.service.UserService
import java.net.URI

@RestController
class UserController(
    val userService: UserService
) {

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