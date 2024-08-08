package sj.messenger.global.exception

import org.springframework.http.HttpStatus

abstract class SimpleMessengerException(
    val status : HttpStatus,
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message) {

}

