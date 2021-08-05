package com.carservice.configurations

import com.carservice.configurations.properties.CarNameAPIConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

/**
 * WebClient configuration
 * Note: application connects to single external API making this configuration more than sufficient
 */
@Configuration
class WebClientConfiguration(
    val carNameAPIConnection: CarNameAPIConnection
) {

    /**
     * @param[webclientBuilder] used to configure the [WebClient]
     * @return [WebClient] bean configured to connect to Car Name API
     */
    @Bean
    fun webClient(webclientBuilder: WebClient.Builder): WebClient =
        webclientBuilder
            .baseUrl(carNameAPIConnection.baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
}
