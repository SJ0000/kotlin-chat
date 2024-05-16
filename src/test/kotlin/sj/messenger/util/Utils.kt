package sj.messenger.util

import java.time.LocalDateTime

fun truncateMicroSeconds(dateTime: LocalDateTime): LocalDateTime{
    val withOutMicro = (dateTime.nano / 1000000) * 1000000
    return dateTime.withNano(withOutMicro)
}