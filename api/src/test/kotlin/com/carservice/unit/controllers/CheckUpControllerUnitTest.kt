package com.carservice.unit.controllers

import com.carservice.configurations.WebSecurityConfiguration
import com.carservice.api.CheckUpController
import com.carservice.api.assemblers.CarCheckUpResourceAssembler
import com.carservice.exceptions.CheckUpNotFoundException
import com.carservice.models.resources.CheckUpResource
import com.carservice.models.views.CheckUpView
import com.carservice.services.CheckUpService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime

@WebMvcTest(controllers = [CheckUpController::class])
@Import(value = [WebSecurityConfiguration::class])
@WithMockUser(authorities = ["SCOPE_ADMIN"])
class CheckUpControllerUnitTest @Autowired constructor(
    private val mvc: MockMvc
) {

    @MockkBean
    lateinit var checkUpService: CheckUpService

    @MockkBean
    lateinit var resourceAssembler: CarCheckUpResourceAssembler

    @Test
    fun `retrieving single check-up should return valid check-up`() {
        val checkUpView = CheckUpView(
            id = 2,
            carId = 2,
            dateTime = LocalDateTime.now(),
            worker = "Duro D.",
            price = 500.00
        )

        every {
            checkUpService.findCheckUp(2)
        } returns checkUpView

        every {
            resourceAssembler.toModel(checkUpView)
        } returns CheckUpResource(
            id = checkUpView.id,
            dateTime = checkUpView.dateTime,
            worker = checkUpView.worker,
            price = checkUpView.price
        )

        mvc.get("/api/v1/checkups/2") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(2) }
            jsonPath("$.worker") { value("Duro D.") }
            jsonPath("$.price") { value(500.00) }
        }
    }

    @Test
    fun `retrieving single, non-existing check-up should return status 404 Not Found`() {
        every {
            checkUpService.findCheckUp(1)
        } throws CheckUpNotFoundException(1)

        mvc.get("/api/v1/checkups/1")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `retrieving last performed check-ups should return check-ups with dates before current date`() {
        every {
            checkUpService.lastPerformedCheckUps(10)
        } returns listOf(
            CheckUpView(
                id = 1,
                carId = 0,
                dateTime = LocalDateTime.now(),
                worker = "Aeoki H.",
                price = 3480.00
            )
        )

        mvc.get("/api/v1/checkups/last") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { MediaType.APPLICATION_JSON }
            jsonPath("$.[0].worker") { value("Aeoki H.") }
        }
    }

    @Test
    fun `retrieving upcoming check-ups should return check-ups with dates ahead of current date`() {
        every {
            checkUpService.upcomingCheckUps(any())
        } returns listOf()

        mvc.get("/api/v1/checkups/upcoming?duration=week") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { MediaType.APPLICATION_JSON }
            jsonPath("$.*") { doesNotExist() }
        }

        mvc.get("/api/v1/checkups/upcoming") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { MediaType.APPLICATION_JSON }
            jsonPath("$.*") { doesNotExist() }
        }

        mvc.get("/api/v1/checkups/upcoming?duration=half-year") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { MediaType.APPLICATION_JSON }
            jsonPath("$.*") { doesNotExist() }
        }
    }

    @Test
    fun `retrieving upcoming check-ups with invalid duration should return status 400 Bad Request`() {
        mvc.get("/api/v1/checkups/upcoming?duration=year")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deleting check-up should return status 204 No Content`() {
        every {
            checkUpService.deleteById(92)
        } returns Unit

        mvc.delete("/api/v1/checkups/92")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `deleting non-existing check-up should return status 404 Not Found`() {
        every {
            checkUpService.deleteById(84)
        } throws CheckUpNotFoundException(84)

        mvc.delete("/api/v1/checkups/84")
            .andExpect {
                status { isNotFound() }
            }
    }
}
