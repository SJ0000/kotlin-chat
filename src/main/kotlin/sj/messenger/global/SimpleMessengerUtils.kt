package sj.messenger.global

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.messaging.simp.stomp.StompHeaderAccessor


fun extractBearerToken(request: HttpServletRequest) : String?{
    val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
    return parseBearerToken(authorizationHeader)
}

fun extractBearerToken(accessor: StompHeaderAccessor) : String?{
    val authorizationHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION) ?: return null
    return parseBearerToken(authorizationHeader)
}

private fun parseBearerToken(header: String): String? {
    if (!header.startsWith("Bearer "))
        return null
    return header.substring("Bearer ".length)
}