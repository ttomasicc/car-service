package com.carservice.models.views

import com.carservice.domain.CheckUp
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

/**
 * [CheckUpView] acts as a DTO for [CheckUp] responses
 */
data class CheckUpView(
    val id: Long,
    val carId: Long,
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val dateTime: LocalDateTime,
    val worker: String,
    val price: Double
) {

    constructor(checkUp: CheckUp, carId: Long) : this(
        id = checkUp.id,
        carId = carId,
        dateTime = checkUp.dateTime,
        worker = checkUp.worker,
        price = checkUp.price
    )
}
