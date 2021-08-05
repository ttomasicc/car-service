package com.carservice.exceptions

import com.carservice.domain.Car

/**
 * Thrown when an application attempts to access non-existing [Car]
 */
class CarNotFoundException(
    id: Long
) : RuntimeException("Car with ID $id not found")
