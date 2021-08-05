package com.carservice.models.requests

import com.carservice.domain.Car
import com.carservice.domain.CarName
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer
import java.time.Year
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * [CarRequest] acts as a DTO for [Car] requests
 *
 * @property[ownerId] Owner ID
 * @property[manufacturer] Manufacturer name
 * @property[model] Model name
 * @property[productionYear] The year the car was produced
 * @property[serialNumber] Unique car serial number
 */
data class CarRequest(
    @field:Min(value = 1, message = "Owner ID must be a positive value")
    val ownerId: Long,
    @field:NotBlank(message = "Manufacturer name cannot be blank")
    @field:Size(max = 50, message = "Manufacturer name must contain maximum 50 characters")
    val manufacturer: String,
    @field:NotBlank(message = "Model name cannot be blank")
    @field:Size(max = 50, message = "Model name must contain maximum 50 characters")
    val model: String,
    @JsonSerialize(using = YearSerializer::class)
    @JsonDeserialize(using = YearDeserializer::class)
    val productionYear: Year,
    @field:Pattern(
        regexp = "^\\d{1,6}$",
        message = "The serial number '\${validatedValue}' must contain 1 to 6 digits only"
    )
    val serialNumber: String
) {

    fun toCar(carNameFetcher: () -> CarName): Car =
        Car(
            ownerId = ownerId,
            productionYear = productionYear,
            serialNumber = serialNumber,
            name = carNameFetcher.invoke()
        )
}
