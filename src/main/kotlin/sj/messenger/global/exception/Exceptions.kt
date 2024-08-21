package sj.messenger.global.exception


import org.springframework.http.HttpStatus.*
import sj.messenger.global.exception.ErrorCode.*

class UnauthorizedException(message: String) : SimpleMessengerException(UNAUTHORIZED, HAS_NO_PERMISSION, message)
class ExpiredFcmTokenException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_EXPIRED)
class FcmTokenAlreadyExistsException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_ALREADY_EXISTS)