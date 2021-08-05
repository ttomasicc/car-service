package com.carservice.api

import com.carservice.api.assemblers.CarResourceAssembler
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.IllegalCarException
import com.carservice.models.requests.CarRequest
import com.carservice.models.resources.CarResource
import com.carservice.models.views.CarView
import com.carservice.services.CarService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

/**
 * REST controller for cars
 */
@RestController
@RequestMapping(value = ["/api/v1/cars"])
class CarController(
    private val carService: CarService,
    private val resourceAssembler: CarResourceAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<CarView>
) {

    @PostMapping
    fun saveCar(
        @Valid @RequestBody carRequest: CarRequest,
        bindingResult: BindingResult
    ): ResponseEntity<Unit> =
        try {
            if (bindingResult.hasErrors())
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    bindingResult.fieldErrors.map { it.defaultMessage }.joinToString(separator = "; ")
                )

            val location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(carService.save(carRequest))
                .toUri()

            ResponseEntity.created(location).build()
        } catch (illegalCarEx: IllegalCarException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, illegalCarEx.message, illegalCarEx)
        }

    @GetMapping(path = ["/{id}"])
    fun findCar(
        @PathVariable id: Long
    ): ResponseEntity<CarResource> =
        try {
            ResponseEntity.ok(
                resourceAssembler.toModel(carService.findCarById(id))
            )
        } catch (carNotFoundEx: CarNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, carNotFoundEx.message, carNotFoundEx)
        }

    @GetMapping
    fun findAllCars(
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<PagedModel<CarResource>> {
        val carsPage = carService.findAllCars(pageable)
        return if (carsPage.content.isNotEmpty())
            ResponseEntity.ok(
                pagedResourcesAssembler.toModel(
                    carsPage,
                    resourceAssembler
                )
            )
        else
            ResponseEntity.ok().build()
    }

    @DeleteMapping(value = ["/{id}"])
    fun deleteCarById(
        @PathVariable id: Long
    ): ResponseEntity<Unit> =
        try {
            carService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (carNotFoundEx: CarNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, carNotFoundEx.message, carNotFoundEx)
        }
}
