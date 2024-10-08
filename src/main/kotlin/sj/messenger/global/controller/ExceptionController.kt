package sj.messenger.global.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import sj.messenger.global.dto.ErrorResponse
import sj.messenger.global.exception.ErrorCode
import sj.messenger.global.exception.SimpleMessengerException

private val logger = KotlinLogging.logger { }

@RestControllerAdvice
class ExceptionController {

    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(400, "요청 파라미터가 잘못되었습니다.")
        exception.fieldErrors.forEach {
            response.addError(it.field, it.defaultMessage ?: "")
        }

        return ResponseEntity.badRequest()
            .body(response)
    }

    @ExceptionHandler(SimpleMessengerException::class)
    fun handleSimpleMessengerException(exception: SimpleMessengerException): ResponseEntity<ErrorResponse> {
        logger.error(exception) { exception.message }
        return ResponseEntity
            .status(exception.status)
            .body(ErrorResponse.of(exception.errorCode))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        logger.error(exception) { exception.message }
        return ResponseEntity.badRequest().body(
            ErrorResponse.of(ErrorCode.UNKNOWN_ERROR)
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(exception: NoResourceFoundException, request: HttpServletRequest) : ResponseEntity<ErrorResponse>{
        logger.error { "${exception.message}. path = ${request.requestURI}" }
        return ResponseEntity.badRequest().body(
            ErrorResponse.of(ErrorCode.UNKNOWN_ERROR)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        logger.error(exception) { "알 수 없는 예외가 발생하였습니다. : ${exception.message}" }
        return ResponseEntity.badRequest().body(
            ErrorResponse.of(ErrorCode.UNKNOWN_ERROR)
        )
    }
}