package com.carservice.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain

/**
 * Application Web Security Configuration
 *
 * Application has two scopes - USER and ADMIN - while also exposing
 * info endpoints that are available to public
 *
 * Requests are authorized by external OAuth2.0 Resource Server using 10minute JWT tokens
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfiguration {

    /**
     * @param[http] overrides default [HttpSecurity] with my custom authorization needs
     * @return customized [SecurityFilterChain] bean
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }
            csrf { disable() }

            oauth2ResourceServer { jwt { } }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

            authorizeRequests {
                // Car endpoints
                authorize(HttpMethod.POST, "/api/v1/cars", hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER"))
                authorize(HttpMethod.GET, "/api/v1/cars", hasAuthority("SCOPE_ADMIN"))
                authorize(HttpMethod.GET, "/api/v1/cars/*", hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER"))
                authorize(HttpMethod.DELETE, "/api/v1/cars/*", hasAuthority("SCOPE_ADMIN"))

                // Car Check-up endpoints
                authorize(HttpMethod.POST, "/api/v1/cars/*/checkups", hasAuthority("SCOPE_ADMIN"))
                authorize(HttpMethod.GET, "/api/v1/cars/*/checkups", hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER"))

                // Check-up endpoints
                authorize("/api/v1/checkups/*", hasAuthority("SCOPE_ADMIN"))

                // Info endpoints
                authorize("/api/v1/info/car-names", permitAll)
                authorize("/api/v1/info/car-names/used", permitAll)

                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }
}
