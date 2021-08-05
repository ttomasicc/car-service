package com.external.OAuth2ResourceServer.controllers

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetails
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionFactory
import com.nimbusds.oauth2.sdk.id.Audience
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Identifier
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.oauth2.sdk.id.JWTID
import com.nimbusds.oauth2.sdk.id.Subject
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import com.nimbusds.oauth2.sdk.token.RefreshToken
import com.nimbusds.oauth2.sdk.token.Tokens
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Provider
import java.time.Duration
import java.time.Instant
import java.util.Random
import java.util.Date

@RestController
class JWTController {
    private val random: Random = Random()
    private val issuer: Issuer = Issuer.parse("https://github.com/soundwalk")
    private val jwk: RSAKey = try {
        RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID(Identifier().value)
            .generate()
    } catch (ex: JOSEException) {
        throw RuntimeException(ex);
    }

    @RequestMapping(path = ["/jwks.json"])
    fun jwkSetEndpoint(): ResponseEntity<String> =
        ResponseEntity
            .ok()
            .contentType(MediaType.valueOf("application/jwk-set+json"))
            .body(JWKSet(jwk).toPublicJWKSet().toString())

    @RequestMapping(path = ["/token"])
    @Throws(JOSEException::class)
    fun tokenEndpoint(
        @RequestParam(required = false) scope: String?
    ): ResponseEntity<String> =
        ResponseEntity
            .ok().contentType(MediaType.APPLICATION_JSON)
            .body(
                AccessTokenResponse(
                    Tokens(createAccessToken(scope), null as RefreshToken?)
                ).toJSONObject().toString()
            )

    @Throws(JOSEException::class)
    private fun createAccessToken(scope: String?): BearerAccessToken {
        val now = Instant.now()
        val lifetime: Duration = Duration.ofMinutes(10L)
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["client_id"] = ClientID()
        if (scope != null) {
            claims["scope"] = scope
        }

        val details = JWTAssertionDetails(
            issuer,
            Subject(if (random.nextBoolean()) "Alice" else "Bob"),
            Audience.create(issuer.value),
            Date.from(now.plus(lifetime)),
            null as Date?,
            Date.from(now),
            JWTID(),
            claims
        )

        val accessToken = JWTAssertionFactory.create(
            details,
            JWSAlgorithm.RS256,
            jwk.toRSAPrivateKey(),
            jwk.keyID,
            null as Provider?
        )

        return BearerAccessToken(accessToken.serialize(), lifetime.seconds, Scope.parse(scope))
    }
}