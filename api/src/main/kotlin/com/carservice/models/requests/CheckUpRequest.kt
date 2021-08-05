package com.carservice.models.requests

import com.carservice.domain.Car
import com.carservice.domain.CheckUp
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

/**
 * [CheckUpRequest] acts as a DTO for [CheckUp] requests
 *
 * @property[carId] Car ID
 * @property[dateTime] Date and time when the check-up was performed
 * @property[worker] Worker's name
 * @property[price] Cost of the check-up service
 */
data class CheckUpRequest(
    val carId: Long,
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val dateTime: LocalDateTime,
    @field:NotBlank(message = "Worker's name cannot be blank")
    val worker: String,
    val price: Double
) {

    fun toCheckUp(carFetcher: (Long) -> Car): CheckUp =
        CheckUp(
            dateTime = dateTime,
            worker = worker,
            price = price,
            car = carFetcher.invoke(carId)
        )
}
