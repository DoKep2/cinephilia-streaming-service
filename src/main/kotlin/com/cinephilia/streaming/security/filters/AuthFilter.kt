package com.cinephilia.streaming.security.filters

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.cinephilia.streaming.security.models.AuthToken
import com.cinephilia.streaming.security.services.CustomUserInfoService
import org.springframework.security.core.context.SecurityContextHolder

@Component
class AuthFilter(
//    private var userInfoService: CustomUserInfoService
) : OncePerRequestFilter() {
    private val TOKEN_HEADER = HttpHeaders.AUTHORIZATION
    private val TOKEN_PREFIX = "Auth "

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractToken(request)

        if (token == null) {

            SecurityContextHolder.getContext().authentication = AuthToken(authenticated = false, null, null)
            filterChain.doFilter(request, response)
            return
        }

//        this.userInfoService.loadUserInfo(token)
        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader(TOKEN_HEADER)
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length)
        }
        return null
    }
}