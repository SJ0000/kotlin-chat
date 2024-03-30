package sj.messenger.global.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sj.messenger.global.dto.ErrorResponse
import java.lang.Exception

@RestControllerAdvice
class ExceptionController {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException) : ResponseEntity<ErrorResponse>{
        val message = exception.message ?: "Bad Request"
        return ResponseEntity.badRequest().body(
            ErrorResponse(400, message)
        )
    }
}