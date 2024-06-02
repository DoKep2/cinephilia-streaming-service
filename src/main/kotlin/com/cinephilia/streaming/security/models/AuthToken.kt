package com.cinephilia.streaming.security.models

import com.cinephilia.streaming.security.models.AuthUser
import org.springframework.security.authentication.AbstractAuthenticationToken

class AuthToken(
    authenticated: Boolean,
    private val principal: AuthUser?,
    private val jwtToken: String?,
) : AbstractAuthenticationToken(null) {
    init {
        isAuthenticated = authenticated
    }

    override fun getCredentials(): String? {
        return this.jwtToken
    }

    override fun getName(): String? {
        return this.principal?.username
    }

    override fun getPrincipal(): AuthUser? {
        return this.principal
    }
}