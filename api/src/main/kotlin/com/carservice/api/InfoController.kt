package com.carservice.api

import com.carservice.models.views.CarNameView
import com.carservice.services.InfoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for general information
 */
@RestController
@RequestMapping(value = ["/api/v1/info"])
class InfoController(
    private val infoService: InfoService
) {

    @GetMapping(path = ["/car-names/used"])
    fun findUsedCarNames(): ResponseEntity<List<CarNameView>> =
        ResponseEntity.ok(infoService.findUsedCarNames())

    @GetMapping(path = ["/car-names"])
    fun findCarNames(
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<Page<CarNameView>> =
        ResponseEntity.ok(infoService.findAllCarNames(pageable))
}
