package sj.messenger.global.exception


import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import sj.messenger.global.exception.ErrorCode.*
import kotlin.reflect.KClass

class UnauthorizedException(message: String) : SimpleMessengerException(UNAUTHORIZED, HAS_NO_PERMISSION, message)

class ExpiredFcmTokenException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_EXPIRED)

class FcmTokenAlreadyExistsException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_ALREADY_EXISTS)

class EntityNotFoundException(targetClass: KClass<*>, keyType: String, keyValue: Any) :
    SimpleMessengerException(NOT_FOUND, ENTITY_NOT_FOUND, "${targetClass.simpleName} not found. ${keyType} = ${keyValue}")
