package com.cinephilia.streaming.security.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class AuthUser(
    val deleted: Boolean,
    val id: UUID,
    @JsonProperty("userName") val username: String,
    @JsonProperty("birthDate") val birthdate: Int,
    val email: String,
    val password: String,
)