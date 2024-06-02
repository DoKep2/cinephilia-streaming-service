package com.cinephilia.streaming

import com.cinephilia.streaming.security.filters.AuthFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
class StreamingApplicationProperties(
    @Value("\${application.protocol}") val protocol: String,
    @Value("\${application.userUrl}") val userUrl: String,
    @Value("\${application.userPath}") val userPath: String,
    @Value("\${application.techUsername}") var techUsername: String,
    @Value("\${application.techPassword}") var techPassword: String,
    private val authorizationFilter: AuthFilter) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .antMatchers("/api/v1/streaming/swagger-ui/**", "/api/v1/streaming/swagger/api-doc/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
            }
            .addFilterBefore(authorizationFilter, BasicAuthenticationFilter::class.java)
    }
}