package com.carservice.models.views

import com.carservice.domain.CarName

/**
 * [CarNameView] acts as a DTO for [CarName] responses
 */
data class CarNameView(
    val manufacturer: String,
    val model: String
) {

    constructor(carName: CarName) : this(
        manufacturer = carName.manufacturer,
        model = carName.model
    )
}
