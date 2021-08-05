package com.carservice.exceptions

import com.carservice.domain.CheckUp

/**
 * Thrown when an application attempts to access non-existing [CheckUp]
 */
class CheckUpNotFoundException(
    id: Long
) : RuntimeException("Check-up with ID $id not found")
