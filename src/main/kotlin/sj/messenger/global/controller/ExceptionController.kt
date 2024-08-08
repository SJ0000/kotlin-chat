package sj.messenger.global.controller

import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import sj.messenger.global.dto.ErrorResponse
import sj.messenger.global.exception.SimpleMessengerException

@RestControllerAdvice
class ExceptionController {

    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<ErrorResponse> {
        val message = exception.message;
        val response = ErrorResponse(400, "요청 파라미터가 잘못되었습니다.")
        exception.fieldErrors.forEach {
            response.addError(it.field, it.defaultMessage ?: "")
        }

        return ResponseEntity.badRequest().body(
            response
        )
    }

    @ExceptionHandler(SimpleMessengerException::class)
    fun handleSimpleMessengerException(exception: SimpleMessengerException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(exception.status)
            .body(ErrorResponse(exception.errorCode.code, exception.message!!))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        val message = exception.message ?: "Bad Request"
        return ResponseEntity.badRequest().body(
            ErrorResponse(400, message)
        )
    }
}