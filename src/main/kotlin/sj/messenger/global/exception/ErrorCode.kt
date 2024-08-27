package sj.messenger.global.exception

enum class ErrorCode(
    val code: Int,
    val message: String,
) {
    HAS_NO_PERMISSION(1,"허용되지 않은 접근입니다."),
    ENTITY_NOT_FOUND(2, "요청한 데이터를 찾을 수 없습니다."),
    FCM_TOKEN_EXPIRED(101, "유효기간이 만료된 FCM 토큰입니다. 갱신이 필요합니다."),
    FCM_TOKEN_ALREADY_EXISTS(102, "사용자는 다른 FCM 토큰을 가지고 있습니다."),
}