package com.carservice.integration

import com.carservice.models.external.CarNameAPIResponse
import com.carservice.models.external.CarNameDescription
import com.carservice.tasks.CarNameSchedulingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import org.mockserver.springtest.MockServerTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles(profiles = ["integration"])
@MockServerTest
class CarNameAPIIntegrationTest @Autowired constructor(
    private val carNameSchedulingService: CarNameSchedulingService
) {

    lateinit var mockServerClient: MockServerClient

    @Test
    fun `verifies correct API mapping`() {
        mockServerClient.`when`(
            HttpRequest.request().withPath("/api/v1/cars")
        ).respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(
                    """
                    {
                      "data":[
                        {
                          "manufacturer":"Cadillac",
                          "model_name":"CTS Coupe",
                          "is_common":1
                        },
                        {
                          "manufacturer":"Rolls-Royce",
                          "model_name":"Silver Spur",
                          "is_common":1
                        },
                        {
                          "manufacturer":"Audi",
                          "model_name":"RS4",
                          "is_common":1
                        }
                      ]
                    }
                    """.trimIndent()
                )
        )

        assertThat(carNameSchedulingService.fetchCarNames())
            .isEqualTo(
                CarNameAPIResponse(
                    listOf(
                        CarNameDescription("Cadillac", "CTS Coupe"),
                        CarNameDescription("Rolls-Royce", "Silver Spur"),
                        CarNameDescription("Audi", "RS4")
                    )
                )
            )
    }
}
