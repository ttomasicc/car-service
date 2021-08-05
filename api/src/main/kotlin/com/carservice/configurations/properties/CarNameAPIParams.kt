package com.carservice.configurations.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.validation.constraints.NotBlank

/**
 * Data class representing the `com.carservice.car-name-api` `namespace`
 * from the properties files; performs basic validation
 *
 * @property[baseUrl] Base URL to the Car Name API
 */
@ConfigurationProperties(prefix = "com.carservice.car-name-api")
@ConstructorBinding
data class CarNameAPIParams(
    @NotBlank
    val baseUrl: String
)
