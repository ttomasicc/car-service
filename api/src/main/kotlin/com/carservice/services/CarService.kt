package com.carservice.services

import com.carservice.domain.Car
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.IllegalCar
import com.carservice.exceptions.IllegalCarException
import com.carservice.models.requests.CarRequest
import com.carservice.models.views.CarView
import com.carservice.repositories.CarNameRepository
import com.carservice.repositories.CarRepository
import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * [CarService] manages car-related requests
 */
@Service
class CarService(
    private val carRepository: CarRepository,
    private val carNameRepository: CarNameRepository
) {

    companion object : KLogging()

    /**
     * Saves given [CarRequest]
     *
     * @param[carRequest] incoming car request
     * @return generated car ID
     * @throws [IllegalCarException]
     */
    @Transactional
    fun save(carRequest: CarRequest): Long {
        if (carRepository.existsBySerialNumber(carRequest.serialNumber))
            throw IllegalCarException(IllegalCar.SERIAL_NUMBER_UNIQUE.errorMessage)

        val carName = carNameRepository.findByManufacturerIgnoreCaseAndModelIgnoreCase(
            manufacturer = carRequest.manufacturer,
            model = carRequest.model
        ) ?: throw IllegalCarException(IllegalCar.MANUFACTURER_MODEL_NOT_FOUND.errorMessage)

        logger.info { "Saving car" }
        val car = carRepository.save(carRequest.toCar { carName })
        logger.info { "Saved car was generated with ID ${car.id}" }

        return car.id
    }

    /**
     * Finds [Car] for with given [id]
     *
     * @param[id] car ID to be searched
     * @return single [CarView]
     * @throws [CarNotFoundException]
     */
    @Transactional(readOnly = true)
    fun findCarById(id: Long): CarView {
        logger.info { "Searching for car with ID $id" }
        val car = carRepository.findById(id) ?: throw CarNotFoundException(id)
        return CarView(
            car = car,
            carName = car.name
        )
    }

    /**
     * Finds all [Car] in the database
     *
     * @param[pageable] pagination and sorting criteria
     * @return [Page] containing (paged) [CarView]
     */
    @Transactional(readOnly = true)
    fun findAllCars(pageable: Pageable): Page<CarView> {
        logger.info {
            """
            Fetching all cars
            Size: ${pageable.pageSize}
            Page: ${pageable.pageNumber}
            """.trimIndent()
        }
        return carRepository.findAll(pageable).map { car ->
            CarView(
                car = car,
                carName = car.name
            )
        }
    }

    /**
     * Deletes single [Car] entity
     *
     * @param[id] car ID
     * @throws [CarNotFoundException]
     */
    @Transactional
    fun deleteById(id: Long) {
        if (carRepository.existsById(id).not())
            throw CarNotFoundException(id)
        logger.info { "Deleting car with ID $id" }
        carRepository.deleteById(id)
        logger.info { "Successfully deleted car with ID $id" }
    }
}
