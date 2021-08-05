package com.carservice.api.advice

import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.sql.SQLException

/**
 * Controller Advice catches all `unexpected` exceptions
 */
@RestControllerAdvice
class ControllerAdvice {

    companion object : KLogging()

    /**
     * Exception Handler for all (SQL) database exceptions
     *
     * @param[sqlEx] general `unexpected` SQL Exception that should be caught now
     * @return [ResponseEntity] informing the user that something went wrong on the server side
     */
    @ExceptionHandler()
    fun handleDatabaseExceptions(sqlEx: SQLException): ResponseEntity<String> {
        logger.warn { "Controller Advice caught unexpected SQL Exception" }
        return ResponseEntity(
            sqlEx.message ?: "Unexpected error occurred, please try again later or contact the support line.",
            HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
