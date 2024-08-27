package sj.messenger.global.exception


import org.springframework.http.HttpStatus.*
import sj.messenger.global.exception.ErrorCode.*
import kotlin.reflect.KClass

class AuthorizeFailedException(message: String) : SimpleMessengerException(UNAUTHORIZED, AUTHORIZE_FAILED, message)

class PermissionDeniedException(targetClass: KClass<*>, targetId: Long, accessorId: Long) :
    SimpleMessengerException(UNAUTHORIZED, PERMISSION_DENIED,
        "Has No Permission. Target = ${targetClass.simpleName}, id = ${targetId}, accessorId = ${accessorId}"
    )

class ExpiredFcmTokenException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_EXPIRED)

class FcmTokenAlreadyExistsException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_ALREADY_EXISTS)

class EntityNotFoundException(targetClass: KClass<*>, keyType: String, keyValue: Any) :
    SimpleMessengerException(
        NOT_FOUND,
        ENTITY_NOT_FOUND,
        "${targetClass.simpleName} not found. ${keyType} = ${keyValue}"
    )

class AlreadyExistsException(targetClass: KClass<*>, description: String) :
    SimpleMessengerException(BAD_REQUEST, ALREADY_EXISTS, "${targetClass.simpleName} already exists. ${description}") {
    constructor(targetClass: KClass<*>, keyType: String, keyValue: Any) : this(
        targetClass,
        "${targetClass.simpleName} already exists. ${keyType} = ${keyValue}"
    )
}


