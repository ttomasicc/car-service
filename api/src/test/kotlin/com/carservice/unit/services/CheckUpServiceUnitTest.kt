package com.carservice.unit.services

import com.carservice.domain.Car
import com.carservice.domain.CarName
import com.carservice.domain.CheckUp
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.CheckUpNotFoundException
import com.carservice.exceptions.IllegalCheckUpException
import com.carservice.models.requests.CheckUpRequest
import com.carservice.models.views.CheckUpView
import com.carservice.repositories.CarRepository
import com.carservice.repositories.CheckUpRepository
import com.carservice.services.CheckUpService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

@ExtendWith(MockKExtension::class)
class CheckUpServiceUnitTest {

    @MockK
    lateinit var carRepository: CarRepository

    @MockK
    lateinit var checkUpRepository: CheckUpRepository

    @InjectMockKs
    lateinit var checkUpService: CheckUpService

    @Test
    fun `saving check-up should return generated check-up id`() {
        val car = Car(
            id = 2,
            ownerId = 42,
            productionYear = Year.now(),
            serialNumber = "249872",
            name = CarName(
                manufacturer = "Alvis",
                model = "TB 21"
            )
        )

        val checkUpReq = CheckUpRequest(
            carId = 2,
            dateTime = LocalDateTime.parse("2020-02-02T02:02:02"),
            worker = "Joshua L. F.",
            price = 387.00
        )

        every {
            carRepository.findById(car.id)
        } returns car

        every {
            checkUpRepository.save(any())
        } returns checkUpReq.toCheckUp { car }.copy(id = 2)

        assertThat(checkUpService.save(checkUpReq))
            .isEqualTo(2L)
    }

    @Test
    fun `saving check-up for non-existing car should throw CarNotFoundException`() {
        every {
            carRepository.findById(92)
        } throws CarNotFoundException(92)

        assertThrows<CarNotFoundException> {
            checkUpService.save(
                CheckUpRequest(
                    carId = 92,
                    dateTime = LocalDateTime.now(),
                    worker = "Dworski S.",
                    price = 39847.43
                )
            )
        }
    }

    @Test
    fun `saving check-up with a negative price should throw IllegalCheckUpException`() {
        assertThrows<IllegalCheckUpException> {
            checkUpService.save(
                CheckUpRequest(
                    carId = 5,
                    dateTime = LocalDateTime.now(),
                    worker = "Jacoco K.",
                    price = -3428.70
                )
            )
        }
    }

    @Test
    fun `retrieving single, non-existing check-up should throw CheckUpNotFoundException`() {
        every {
            checkUpRepository.findById(1)
        } returns null

        assertThrows<CheckUpNotFoundException> {
            checkUpService.findCheckUp(1)
        }
    }

    @Test
    fun `retrieving check-ups for non-existing car should throw CarNotFoundException`() {
        every {
            carRepository.existsById(11).not()
        } returns false

        assertThrows<CarNotFoundException> {
            checkUpService.findAllCarCheckUpsByCarId(11, Pageable.ofSize(10))
        }
    }

    @Test
    fun `retrieving check-ups for a car with no check-ups should return an empty page`() {
        every {
            carRepository.existsById(15).not()
        } returns true

        every {
            checkUpRepository.findAllByCarId(15, any())
        } returns Page.empty()

        assertThat(checkUpService.findAllCarCheckUpsByCarId(15, Pageable.ofSize(10)))
            .isEmpty()
    }

    @Test
    fun `deleting check-up should run without exceptions`() {
        every {
            checkUpRepository.existsById(4)
        } returns true

        every {
            checkUpRepository.deleteById(4)
        } returns Unit

        checkUpService.deleteById(4)

        verify(exactly = 1) { checkUpRepository.deleteById(4) }
    }

    @Test
    fun `deleting non-existing check-up should throw CheckUpNotFoundException`() {
        every {
            checkUpRepository.existsById(7)
        } returns false

        assertThrows<CheckUpNotFoundException> {
            checkUpService.deleteById(7)
        }
    }

    @Test
    fun `retrieving last performed check-ups should return check-ups with dates before current date`() {
        val checkUp = CheckUp(
            id = 1,
            dateTime = LocalDateTime.now(),
            worker = "Aeoki H.",
            price = 3480.00,
            car = Car(
                id = 1,
                ownerId = 42,
                dateCreated = LocalDate.now(),
                productionYear = Year.of(2020),
                serialNumber = "833848",
                name = CarName(
                    manufacturer = "Porsche",
                    model = "Macan"
                )
            )
        )

        every {
            checkUpRepository.lastPerformedCheckUps(10)
        } returns listOf(checkUp)

        assertThat(checkUpService.lastPerformedCheckUps(10))
            .isEqualTo(listOf(CheckUpView(checkUp, checkUp.car.id)))
    }

    @Test
    fun `retrieving upcoming check-ups should return check-ups with dates ahead of current date`() {
        every {
            checkUpRepository.upcomingCheckUps(any())
        } returns listOf()

        assertThat(checkUpService.upcomingCheckUps(LocalDate.now()))
            .isEqualTo(listOf<CheckUpView>())
    }
}
