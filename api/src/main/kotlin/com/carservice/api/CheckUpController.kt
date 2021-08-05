package com.carservice.api

import com.carservice.api.assemblers.CarCheckUpResourceAssembler
import com.carservice.exceptions.CheckUpNotFoundException
import com.carservice.models.resources.CheckUpResource
import com.carservice.models.views.CheckUpView
import com.carservice.services.CheckUpService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

/**
 * REST controller for check-ups
 */
@RestController
@RequestMapping(value = ["/api/v1/checkups"])
class CheckUpController(
    private val checkUpService: CheckUpService,
    private val resourceAssembler: CarCheckUpResourceAssembler
) {

    @GetMapping(path = ["/{id}"])
    fun findCheckUp(
        @PathVariable id: Long
    ): ResponseEntity<CheckUpResource> =
        try {
            ResponseEntity.ok(
                resourceAssembler.toModel(checkUpService.findCheckUp(id))
            )
        } catch (checkUpNotFoundEx: CheckUpNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, checkUpNotFoundEx.message, checkUpNotFoundEx)
        }

    @DeleteMapping(path = ["/{id}"])
    fun deleteCheckUp(
        @PathVariable id: Long
    ): ResponseEntity<CheckUpResource> =
        try {
            checkUpService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (checkUpNotFoundEx: CheckUpNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, checkUpNotFoundEx.message, checkUpNotFoundEx)
        }

    @GetMapping(path = ["/last"])
    fun lastPerformedCheckUps(
        @RequestParam(defaultValue = "10") limit: Long
    ): ResponseEntity<List<CheckUpView>> =
        ResponseEntity.ok(checkUpService.lastPerformedCheckUps(limit))

    @Suppress("MagicNumber")
    @GetMapping(path = ["/upcoming"])
    fun upcomingCheckUps(
        @RequestParam(defaultValue = "month") duration: String
    ): ResponseEntity<List<CheckUpView>> =
        when (duration) {
            "week" -> ResponseEntity.ok(checkUpService.upcomingCheckUps(LocalDate.now().plusWeeks(1)))
            "month" -> ResponseEntity.ok(checkUpService.upcomingCheckUps(LocalDate.now().plusMonths(1)))
            "half-year" -> ResponseEntity.ok(checkUpService.upcomingCheckUps(LocalDate.now().plusMonths(6)))
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Available durations: week, month, half-year")
        }
}
