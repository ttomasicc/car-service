package com.carservice.unit.services

import com.carservice.domain.Car
import com.carservice.domain.CarName
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.IllegalCarException
import com.carservice.models.requests.CarRequest
import com.carservice.models.views.CarView
import com.carservice.repositories.CarNameRepository
import com.carservice.repositories.CarRepository
import com.carservice.services.CarService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.Year

@ExtendWith(MockKExtension::class)
class CarServiceUnitTest {

    @MockK
    lateinit var carRepository: CarRepository

    @MockK
    lateinit var carNameRepository: CarNameRepository

    @InjectMockKs
    lateinit var carService: CarService

    @Test
    fun `saving car should return generated car id`() {
        val carReq = CarRequest(
            ownerId = 42,
            manufacturer = "AMC",
            model = "AMX",
            productionYear = Year.of(2020),
            serialNumber = "333210"
        )

        val carName = CarName(
            manufacturer = "AMC",
            model = "AMX"
        )

        every {
            carRepository.existsBySerialNumber(carReq.serialNumber)
        } returns false

        every {
            carNameRepository.findByManufacturerIgnoreCaseAndModelIgnoreCase(carReq.manufacturer, carReq.model)
        } returns carName

        every {
            carRepository.save(any())
        } returns Car(
            id = 1,
            ownerId = carReq.ownerId,
            productionYear = carReq.productionYear,
            serialNumber = carReq.serialNumber,
            name = carName
        )

        assertThat(carService.save(carReq))
            .isEqualTo(1L)
    }

    @Test
    fun `saving car with existing serial number should throw IllegalCarException`() {
        every {
            carRepository.existsBySerialNumber(any())
        } returns true

        assertThrows<IllegalCarException> {
            carService.save(
                CarRequest(
                    ownerId = 42,
                    manufacturer = "Tesla",
                    model = "S",
                    productionYear = Year.of(2020),
                    serialNumber = "333210"
                )
            )
        }
    }

    @Test
    fun `saving car with invalid manufacturer or model should throw IllegalCarException`() {
        every {
            carRepository.existsBySerialNumber("333210")
        } returns false

        every {
            carNameRepository.findByManufacturerIgnoreCaseAndModelIgnoreCase("Tesla", "S")
        } returns null

        assertThrows<IllegalCarException> {
            carService.save(
                CarRequest(
                    ownerId = 42,
                    manufacturer = "Tesla",
                    model = "S",
                    productionYear = Year.of(2020),
                    serialNumber = "333210"
                )
            )
        }
    }

    @Test
    fun `retrieving single car should return valid car`() {
        val car = Car(
            id = 95,
            ownerId = 39,
            dateCreated = LocalDate.parse("2014-04-14"),
            productionYear = Year.of(2004),
            serialNumber = "091472",
            name = CarName(
                manufacturer = "Cadillac",
                model = "CTS Coupe"
            )
        )

        every {
            carRepository.findById(95)
        } returns car

        assertThat(carService.findCarById(95))
            .isEqualTo(CarView(car, car.name))
    }

    @Test
    fun `retrieving single car that doesn't exist should throw CarNotFoundException`() {
        every {
            carRepository.findById(94)
        } returns null

        assertThrows<CarNotFoundException> {
            carService.findCarById(94)
        }
    }

    @Test
    fun `retrieving all cars when there's no cars should return an empty page`() {
        every {
            carRepository.findAll(any())
        } returns Page.empty()

        assertThat(carService.findAllCars(Pageable.ofSize(10)))
            .isEmpty()
    }

    @Test
    fun `deleting non-existing car should throw CarNotFoundException`() {
        every {
            carRepository.existsById(5)
        } returns false

        assertThrows<CarNotFoundException> {
            carService.deleteById(5)
        }
    }
}
