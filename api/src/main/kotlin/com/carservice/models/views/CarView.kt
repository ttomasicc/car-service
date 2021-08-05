package com.carservice.models.views

import com.carservice.domain.Car
import com.carservice.domain.CarName
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer
import java.time.LocalDate
import java.time.Year

/**
 * [CarView] acts as a DTO for [Car] responses
 */
data class CarView(
    val id: Long,
    val ownerId: Long,
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val dateCreated: LocalDate,
    val manufacturer: String,
    val model: String,
    @JsonSerialize(using = YearSerializer::class)
    @JsonDeserialize(using = YearDeserializer::class)
    val productionYear: Year,
    val serialNumber: String,
) {

    constructor(car: Car, carName: CarName) : this(
        id = car.id,
        ownerId = car.ownerId,
        dateCreated = car.dateCreated,
        manufacturer = carName.manufacturer,
        model = carName.model,
        productionYear = car.productionYear,
        serialNumber = car.serialNumber
    )
}
