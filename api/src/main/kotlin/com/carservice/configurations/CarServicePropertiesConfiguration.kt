package com.carservice.configurations

import com.carservice.configurations.properties.CarNameAPIConnection
import com.carservice.configurations.properties.CarNameAPIParams
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * General application properties configurations
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = [CarNameAPIParams::class])
class CarServicePropertiesConfiguration {

    /**
     * @param[carNameAPIParams] is autowired bean of [CarNameAPIParams] type
     * @return Autoconfigured bean of type [CarNameAPIConnection]
     */
    @Bean
    fun carNameApiConnection(carNameAPIParams: CarNameAPIParams): CarNameAPIConnection =
        CarNameAPIConnection(
            baseUrl = carNameAPIParams.baseUrl
        )
}
