package sj.chat.domain.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import sj.chat.domain.chat.domain.ChatRoom
import sj.chat.domain.chat.dto.ChatRoomDto
import sj.chat.domain.chat.service.ChatService

@RestController
class ChatRestController (
    private val chatService: ChatService,
){

    @GetMapping("/chatrooms/{id}")
    fun getChatRoomInfo(@PathVariable id : Long) : ResponseEntity<ChatRoomDto>{
        val chatRoom = chatService.getChatRoom(id)
        val chatRoomDto = ChatRoomDto(id = chatRoom.id!!, name = chatRoom.name, maxCapacity = chatRoom.maxCapacity)

        return ResponseEntity.ok()
            .body(chatRoomDto)
    }
}