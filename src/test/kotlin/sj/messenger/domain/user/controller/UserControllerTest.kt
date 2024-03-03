package sj.messenger.domain.user.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import sj.messenger.domain.user.repository.UserRepository
import sj.messenger.domain.user.service.UserService

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest(
    @Autowired mockMvc: MockMvc,
    @Autowired userService: UserService,
){

    @Test
    fun user(){

    }



}