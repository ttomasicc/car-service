package com.carservice.unit.controllers

import com.carservice.configurations.WebSecurityConfiguration
import com.carservice.api.CarController
import com.carservice.api.assemblers.CarResourceAssembler
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.IllegalCar
import com.carservice.exceptions.IllegalCarException
import com.carservice.models.requests.CarRequest
import com.carservice.models.resources.CarResource
import com.carservice.models.views.CarView
import com.carservice.services.CarService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.Year

@WebMvcTest(controllers = [CarController::class])
@Import(value = [WebSecurityConfiguration::class])
@WithMockUser(authorities = ["SCOPE_USER"])
class CarControllerUnitTest @Autowired constructor(
    private val mvc: MockMvc
) {

    @MockkBean
    lateinit var carService: CarService

    @MockkBean
    lateinit var carResourceAssembler: CarResourceAssembler

    @Test
    fun `saving car should return status 201 Created with location in header`() {
        val car = CarRequest(
            ownerId = 42,
            manufacturer = "Ferrari",
            model = "Rossa",
            productionYear = Year.of(2020),
            serialNumber = "333210"
        )

        every {
            carService.save(car)
        } returns 1L

        val location = mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(car)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.getHeader("Location") as String

        assertThat(location)
            .endsWith("/api/v1/cars/1")
    }

    @Test
    fun `saving car with existing serial number should return status 400 Bad Request`() {
        val car = CarRequest(
            ownerId = 42,
            manufacturer = "Rimac",
            model = "Nevera",
            productionYear = Year.of(2022),
            serialNumber = "202207"
        )

        every {
            carService.save(car)
        } throws IllegalCarException(IllegalCar.SERIAL_NUMBER_UNIQUE.errorMessage)

        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(car)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `saving car that doesn't meet validation rules should return status 400 Bad Request`() {
        val car = CarRequest(
            ownerId = 42,
            manufacturer = "    ",
            model = " ",
            productionYear = Year.of(2022),
            serialNumber = "847953"
        )

        every {
            carService.save(car)
        } throws ResponseStatusException(HttpStatus.BAD_REQUEST)

        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(car)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `retrieving single car should return valid car`() {
        val carView = CarView(
            id = 5,
            ownerId = 42,
            dateCreated = LocalDate.parse("2014-02-25"),
            manufacturer = "Lamborghini",
            model = "Huracan",
            productionYear = Year.of(2014),
            serialNumber = "902453"
        )

        every {
            carService.findCarById(5)
        } returns carView

        every {
            carResourceAssembler.toModel(carView)
        } returns CarResource(
            carView.id,
            carView.ownerId,
            carView.dateCreated,
            carView.manufacturer,
            carView.model,
            carView.productionYear,
            carView.serialNumber
        )

        mvc.get("/api/v1/cars/5") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(5) }
            jsonPath("$.serialNumber") { value("902453") }
        }
    }

    @Test
    fun `retrieving single car that doesn't exist should return status 404 Not Found`() {
        every {
            carService.findCarById(48)
        } throws CarNotFoundException(48)

        mvc.get("/api/v1/cars/48")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving all cars when there is no cars should return an empty page`() {
        every {
            carService.findAllCars(any())
        } returns Page.empty()

        mvc.get("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.*") { doesNotExist() }
        }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `deleting car should return status 204 No Content`() {
        every {
            carService.deleteById(12)
        } returns Unit

        mvc.delete("/api/v1/cars/12")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `deleting non-existing car should throw status 404 Not Found `() {
        every {
            carService.deleteById(13)
        } throws CarNotFoundException(13)

        mvc.delete("/api/v1/cars/13")
            .andExpect {
                status { isNotFound() }
            }
    }
}
