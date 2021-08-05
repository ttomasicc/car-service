package com.carservice.unit.controllers

import com.carservice.configurations.WebSecurityConfiguration
import com.carservice.api.CarCheckUpController
import com.carservice.api.assemblers.CarCheckUpResourceAssembler
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.IllegalCheckUp
import com.carservice.exceptions.IllegalCheckUpException
import com.carservice.models.requests.CheckUpRequest
import com.carservice.services.CheckUpService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(controllers = [CarCheckUpController::class])
@Import(value = [WebSecurityConfiguration::class])
@WithMockUser(authorities = ["SCOPE_USER"])
class CarCheckUpControllerUnitTest @Autowired constructor(
    private val mvc: MockMvc
) {

    @MockkBean
    lateinit var checkUpService: CheckUpService

    @MockkBean
    lateinit var resourceAssembler: CarCheckUpResourceAssembler

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up should return status 201 Created with location in header`() {
        val checkUp = CheckUpRequest(
            carId = 7,
            dateTime = LocalDateTime.parse("2021-06-06T16:45:12"),
            worker = "Aleks B.",
            price = 272.20
        )

        every {
            checkUpService.save(checkUp)
        } returns 1L

        val location = mvc.post("/api/v1/cars/7/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(checkUp)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.getHeader("Location") as String

        Assertions.assertThat(location)
            .endsWith("/api/v1/cars/7/checkups/1")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up that doesn't meet validation rules should return status 400 Bad Request`() {
        val checkUpBlankWorker = CheckUpRequest(
            carId = 0,
            dateTime = LocalDateTime.parse("2012-12-12T12:12:12"),
            worker = "  ",
            price = 12.12
        )

        mvc.post("/api/v1/cars/1/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(checkUpBlankWorker)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up for non-existing car should return status 404 Not Found`() {
        val checkUp = CheckUpRequest(
            carId = 5,
            dateTime = LocalDateTime.parse("2002-05-06T00:00:00"),
            worker = "Jack T.",
            price = 50.50
        )

        every {
            checkUpService.save(checkUp)
        } throws CarNotFoundException(5)

        mvc.post("/api/v1/cars/5/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(checkUp)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up with negative price should return status 400 Bad Request`() {
        val checkUp = CheckUpRequest(
            carId = 1,
            dateTime = LocalDateTime.parse("2020-04-17T11:25:25"),
            worker = "Marie P.",
            price = -1234.00
        )

        every {
            checkUpService.save(checkUp)
        } throws IllegalCheckUpException(IllegalCheckUp.PRICE_NON_NEGATIVE.errorMessage)

        mvc.post("/api/v1/cars/1/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(checkUp)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `retrieving check-ups for a car with no check-ups should return an empty page`() {
        every {
            checkUpService.findAllCarCheckUpsByCarId(any(), any())
        } returns Page.empty()

        mvc.get("/api/v1/cars/3/checkups") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.*") { doesNotExist() }
        }
    }

    @Test
    fun `retrieving check-ups for non-existing car should return status 404 Not Found`() {
        every {
            checkUpService.findAllCarCheckUpsByCarId(any(), any())
        } throws CarNotFoundException(8)

        mvc.get("/api/v1/cars/8/checkups")
            .andExpect {
                status { isNotFound() }
            }
    }
}
