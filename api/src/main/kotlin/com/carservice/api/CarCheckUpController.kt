package com.carservice.api

import com.carservice.api.assemblers.CarCheckUpResourceAssembler
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.IllegalCheckUpException
import com.carservice.models.requests.CheckUpRequest
import com.carservice.models.resources.CheckUpResource
import com.carservice.models.views.CheckUpView
import com.carservice.services.CheckUpService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
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
 * REST controller for car check-ups
 */
@RestController
@RequestMapping(path = ["/api/v1/cars"])
class CarCheckUpController(
    private val checkUpService: CheckUpService,
    private val resourceAssembler: CarCheckUpResourceAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<CheckUpView>
) {

    @PostMapping(path = ["/{id}/checkups"])
    fun saveCarCheckUp(
        @PathVariable id: Long,
        @Valid @RequestBody checkUpRequest: CheckUpRequest,
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
                .buildAndExpand(checkUpService.save(checkUpRequest.copy(carId = id)))
                .toUri()

            ResponseEntity.created(location).build()
        } catch (illegalCheckUpEx: IllegalCheckUpException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, illegalCheckUpEx.message, illegalCheckUpEx)
        } catch (carNotFoundEx: CarNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, carNotFoundEx.message, carNotFoundEx)
        }

    @GetMapping(path = ["/{id}/checkups"])
    fun findAllCarCheckUps(
        @PathVariable id: Long,
        @PageableDefault(sort = ["dateTime"], direction = Sort.Direction.DESC, size = 10) pageable: Pageable
    ): ResponseEntity<PagedModel<CheckUpResource>> =
        try {
            val checkUpsPage = checkUpService.findAllCarCheckUpsByCarId(id, pageable)
            if (checkUpsPage.content.isNotEmpty())
                ResponseEntity.ok(
                    pagedResourcesAssembler.toModel(
                        checkUpService.findAllCarCheckUpsByCarId(id, pageable),
                        resourceAssembler
                    )
                )
            else
                ResponseEntity.ok().build()
        } catch (carNotFoundEx: CarNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, carNotFoundEx.message, carNotFoundEx)
        }
}
