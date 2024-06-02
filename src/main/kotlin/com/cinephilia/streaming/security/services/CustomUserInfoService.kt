package com.cinephilia.streaming.security.services

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.cinephilia.streaming.security.models.AuthUser
import com.cinephilia.streaming.security.models.AuthToken
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import com.cinephilia.streaming.StreamingApplicationProperties
import com.cinephilia.streaming.security.HttpClientFactory

@Service
class CustomUserInfoService(
    private val objectMapper: ObjectMapper,
    val config: StreamingApplicationProperties,
    private val httpClientFactory: HttpClientFactory
) {
    @Volatile
    private var token: String? = null
    private var httpClient: HttpClient = HttpClient.newHttpClient()
    private val logger = LoggerFactory.getLogger(CustomUserInfoService::class.java)

    fun loadUserInfo(token: String) {
        val userInfoEndpoint = "${config.protocol}://${config.userUrl}/${config.userPath}/user-info"

        val client = httpClientFactory.newTracedClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(userInfoEndpoint))
            .header(HttpHeaders.AUTHORIZATION, "Auth $token")
            .GET()
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == 200) {
            val user = objectMapper.readValue(response.body().toString().trimIndent(), AuthUser::class.java)
            logger.info("Loaded user-info for the current request: {}", user)

            val authenticationToken = AuthToken(authenticated = true, user, token)
            SecurityContextHolder.getContext().authentication = authenticationToken
        } else {
            logger.error("Failed to get user-info with status code ${response.statusCode()}")
            val authenticationToken = AuthToken(authenticated = false, null, null)
            SecurityContextHolder.getContext().authentication = authenticationToken
        }

    }

    fun getOrFetchTechAccessToken(): String {
        if (hasValidToken()) {
            logger.info("Skip getting the token since a valid access token already exists")
            return token!!
        }

        val loginEndpoint = "${config.protocol}://${config.userUrl}/${config.userPath}/login"

        val body = mapOf("username" to config.techUsername, "password" to config.techPassword)
        val requestBody = objectMapper.writeValueAsString(body)

        val client = httpClientFactory.newTracedClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(loginEndpoint))
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            logger.error("Tech-account token request failed with status code ${response.statusCode()}")
            throw RuntimeException("Failed to get an access token for tech-account: ${response.body()}")
        }

        token = objectMapper.readTree(response.body()).get("accessToken").textValue()
        return token!!
    }

    private fun hasValidToken(): Boolean {
        val existingToken = token ?: return false
        val jwt = JWT.decode(existingToken)

        val skewMillis = 30000 // 30 sec
        val expirationWithSkew = Date(jwt.expiresAt.time + skewMillis)
        return !expirationWithSkew.before(Date())
    }
}