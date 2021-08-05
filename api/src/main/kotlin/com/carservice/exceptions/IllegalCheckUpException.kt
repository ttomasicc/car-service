package com.carservice.exceptions

import com.carservice.domain.CheckUp

/**
 * Thrown when an application attempts to save/update [CheckUp] with invalid data
 */
class IllegalCheckUpException(
    errorMessage: String
) : RuntimeException(errorMessage)

/**
 * Helper enum class for [IllegalCheckUpException] error messages
 */
enum class IllegalCheckUp(
    val errorMessage: String
) {
    PRICE_NON_NEGATIVE("Price must be a non-negative value")
}
