package sj.chat.domain.chat.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import sj.chat.domain.chat.service.ChatService

@Controller
class ChatController (
    private val chatService: ChatService,
){







}