package com.carservice.integration

import com.carservice.models.requests.CarRequest
import com.carservice.models.requests.CheckUpRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockserver.springtest.MockServerTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import java.time.Year

@SpringBootTest
@ActiveProfiles(profiles = ["integration"])
@AutoConfigureMockMvc
@MockServerTest
@Sql(scripts = ["classpath:db/populate_car_names.sql"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@WithMockUser(authorities = ["SCOPE_USER"])
class AppIntegrationTest @Autowired constructor(
    private val mvc: MockMvc
) {

    @Test
    @Order(1)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `car table should be empty on startup`() {
        mvc.get("/api/v1/cars") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.*") { doesNotExist() }
        }
    }

    @Test
    @Order(2)
    fun `saving car should return status 201 Created with location in header`() {
        val car = CarRequest(
            ownerId = 74,
            manufacturer = "Chevrolet",
            model = "Malibu",
            productionYear = Year.of(2021),
            serialNumber = "042749"
        )

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
    @Order(3)
    fun `saving car with existing serial number should return status 400 Bad Request`() {
        val carSerialNumberUnique = CarRequest(
            ownerId = 74,
            manufacturer = "Ferrari",
            model = "Rossa",
            productionYear = Year.of(2021),
            serialNumber = "042749"
        )

        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(carSerialNumberUnique)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Order(4)
    fun `saving car with invalid manufacturer or model should return status 400 Bad Request`() {
        val carInvalidName = CarRequest(
            ownerId = 74,
            manufacturer = "Jaguar",
            model = "Sovereign",
            productionYear = Year.of(2021),
            serialNumber = "423804"
        )

        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(carInvalidName)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Order(5)
    fun `saving car that doesn't meet validation rules should return status 400 Bad Request`() {
        val carSerialNumberSize = CarRequest(
            ownerId = 74,
            manufacturer = "Alvis",
            model = "TB 21",
            productionYear = Year.of(2021),
            serialNumber = "28310292"
        )

        val carBlankModel = CarRequest(
            ownerId = 42,
            manufacturer = "Cadillac",
            model = "  ",
            productionYear = Year.of(1985),
            serialNumber = "45"
        )

        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(carSerialNumberSize)
        }.andExpect {
            status { isBadRequest() }
        }

        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(carBlankModel)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up should return status 201 Created with location in header`() {
        val checkUp = CheckUpRequest(
            carId = 0,
            dateTime = LocalDateTime.parse("1997-12-12T00:05:01"),
            worker = "Zare Y.",
            price = 4500.20
        )

        val location = mvc.post("/api/v1/cars/1/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(checkUp)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.getHeader("Location") as String

        assertThat(location)
            .endsWith("/api/v1/cars/1/checkups/1")
    }

    @Test
    @Order(7)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up for non-existing car should return status 404 Not Found`() {
        mvc.post("/api/v1/cars/5/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(
                CheckUpRequest(
                    carId = 0,
                    dateTime = LocalDateTime.parse("1997-12-12T00:05:01"),
                    worker = "Lukas W.",
                    price = 4500.20
                )
            )
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @Order(8)
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
    @Order(9)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `saving check-up with negative price should return status 400 Bad Request`() {
        val checkUpPricePositive = CheckUpRequest(
            carId = 0,
            dateTime = LocalDateTime.now(),
            worker = "Genie B.",
            price = -3000.25
        )

        mvc.post("/api/v1/cars/1/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(checkUpPricePositive)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @Order(10)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving cars should return paged cars`() {
        mvc.get("/api/v1/cars") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$._embedded.item[0].id") { value(1) }
            jsonPath("$._embedded.item[0].serialNumber") { value("042749") }
            jsonPath("$._embedded.item[0]._links") { isNotEmpty() }
            jsonPath("$.page.number") { value(0) }
        }
    }

    @Test
    @Order(11)
    fun `retrieving single car that doesn't exist should return status 404 Not Found`() {
        mvc.get("/api/v1/cars/3")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Order(12)
    fun `retrieving single car should return valid car`() {
        mvc.get("/api/v1/cars/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(1) }
            jsonPath("$.serialNumber") { value("042749") }
            jsonPath("$._links") { isNotEmpty() }
        }
    }

    @Test
    @Order(13)
    fun `retrieving check-ups for specific car should return valid car check-ups`() {
        mvc.get("/api/v1/cars/1/checkups") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$._embedded.item[0].id") { value(1) }
            jsonPath("$._embedded.item[0].worker") { value("Zare Y.") }
            jsonPath("$._embedded.item[0].price") { value(4500.20) }
            jsonPath("$._embedded.item[0]._links") { isNotEmpty() }
            jsonPath("$.page.number") { value(0) }
        }
    }

    @Test
    @Order(14)
    fun `retrieving check-ups for non-existing car should return status 404 Not Found`() {
        mvc.get("/api/v1/cars/3/checkups")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Order(15)
    fun `retrieving check-ups for newly saved car should return empty response`() {
        mvc.post("/api/v1/cars") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(
                CarRequest(
                    ownerId = 74,
                    manufacturer = "Rolls-Royce",
                    model = "Silver Spur",
                    productionYear = Year.of(2015),
                    serialNumber = "982723"
                )
            )
        }.andExpect {
            status { isCreated() }
        }

        mvc.get("/api/v1/cars/2/checkups")
            .andExpect {
                status { isOk() }
                jsonPath("$.*") { doesNotExist() }
            }
    }

    @Test
    @Order(16)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving single check-up should return valid check-up`() {
        mvc.get("/api/v1/checkups/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(1) }
            jsonPath("$.worker") { value("Zare Y.") }
            jsonPath("$.price") { value(4500.20) }
            jsonPath("$._links") { isNotEmpty() }
        }
    }

    @Test
    @Order(17)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving single, non-existing check-up should return status 404 Not Found`() {
        mvc.get("/api/v1/checkups/3")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Order(18)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving last performed check-ups should return check-ups with dates before current date`() {
        mvc.post("/api/v1/cars/1/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(
                CheckUpRequest(
                    carId = 0,
                    dateTime = LocalDateTime.now().plusMonths(5),
                    worker = "Aeoki H.",
                    price = 3480.00
                )
            )
        }.andExpect {
            status { isCreated() }
        }

        mvc.get("/api/v1/checkups/last") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { MediaType.APPLICATION_JSON }
            jsonPath("$.[0].dateTime") { value("1997-12-12T00:05:01") }
        }
    }

    @Test
    @Order(19)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving upcoming check-ups should return check-ups with dates ahead of current date`() {
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
            jsonPath("$.[0]") { exists() }
            jsonPath("$.[1]") { doesNotExist() }
        }
    }

    @Test
    @Order(20)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `retrieving upcoming check-ups with invalid duration should return status 400 Bad Request`() {
        mvc.get("/api/v1/checkups/upcoming?duration=year")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    @Order(21)
    fun `retrieving car names should return paged car names`() {
        mvc.get("/api/v1/info/car-names") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()") { value(10) }
            jsonPath("$.pageable.pageNumber") { value(0) }
        }
    }

    @Test
    @Order(22)
    fun `retrieving used car names should return list of used car names`() {
        mvc.get("/api/v1/info/car-names/used") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.[0].manufacturer") { value("Rolls-Royce") }
            jsonPath("$.[0].model") { value("Silver Spur") }
            jsonPath("$.[1].manufacturer") { value("Chevrolet") }
            jsonPath("$.[1].model") { value("Malibu") }
        }
    }

    @Test
    @Order(23)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `deleting check-up should return status 204 No Content`() {
        mvc.delete("/api/v1/checkups/2")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @Order(24)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `deleting non-existing check-up should return status 404 Not Found`() {
        mvc.delete("/api/v1/checkups/2")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Order(25)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `deleting car should return status 204 No Content`() {
        mvc.delete("/api/v1/cars/1")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @Order(26)
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    fun `deleting non-existing car should return status 404 Not Found`() {
        mvc.delete("/api/v1/cars/1")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @WithAnonymousUser
    fun `anonymous user should get status 401 Unauthorized when accessing cars`() {
        mvc.get("/api/v1/cars")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `user should get status 403 Forbidden when saving new check-up`() {
        mvc.post("/api/v1/cars/2/checkups") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(
                CheckUpRequest(
                    carId = 12,
                    dateTime = LocalDateTime.now(),
                    worker = "Timmy",
                    price = 2200.00
                )
            )
        }.andExpect {
            status { isForbidden() }
        }
    }
}
