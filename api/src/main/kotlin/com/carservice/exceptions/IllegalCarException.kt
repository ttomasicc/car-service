package com.carservice.exceptions

import com.carservice.domain.Car

/**
 * Thrown when an application attempts to save/update [Car] with invalid data
 */
class IllegalCarException(
    errorMessage: String
) : RuntimeException(errorMessage)

/**
 * Helper enum class for [IllegalCarException] error messages
 */
enum class IllegalCar(
    val errorMessage: String
) {
    SERIAL_NUMBER_UNIQUE("Serial number must be unique"),
    MANUFACTURER_MODEL_NOT_FOUND("Manufacturer or model not supported")
}
