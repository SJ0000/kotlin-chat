package sj.messenger.global.exception

enum class ErrorCode(
    val code: Int,
    val message: String,
) {
    FCM_TOKEN_EXPIRED(1, "유효기간이 만료된 FCM 토큰입니다. 갱신이 필요합니다."),
    FCM_TOKEN_ALREADY_EXISTS(2, "사용자는 다른 FCM 토큰을 가지고 있습니다."),
}