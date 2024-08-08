package sj.messenger.global.exception


import org.springframework.http.HttpStatus.*
import sj.messenger.global.exception.ErrorCode.*


class ExpiredFcmTokenException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_EXPIRED)
class FcmTokenAlreadyExistsException() : SimpleMessengerException(BAD_REQUEST, FCM_TOKEN_ALREADY_EXISTS)
