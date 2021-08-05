package com.carservice.unit.controllers

import com.carservice.configurations.WebSecurityConfiguration
import com.carservice.api.InfoController
import com.carservice.domain.CarName
import com.carservice.models.views.CarNameView
import com.carservice.services.InfoService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(controllers = [InfoController::class])
@Import(value = [WebSecurityConfiguration::class])
@WithAnonymousUser
class InfoControllerUnitTest @Autowired constructor(
    private val mvc: MockMvc
) {

    @MockkBean
    lateinit var infoService: InfoService

    @Test
    fun `retrieving used car names should return list of used car names`() {
        val carName = CarName(12, "Porsche", "Taycan Turbo S")

        every {
            infoService.findUsedCarNames()
        } returns listOf(CarNameView(carName))

        mvc.get("/api/v1/info/car-names/used") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.[0].manufacturer") { value("Porsche") }
            jsonPath("$.[0].model") { value("Taycan Turbo S") }
        }
    }
}
