package com.carservice.services

import com.carservice.domain.Car
import com.carservice.domain.CheckUp
import com.carservice.exceptions.CarNotFoundException
import com.carservice.exceptions.CheckUpNotFoundException
import com.carservice.exceptions.IllegalCheckUp
import com.carservice.exceptions.IllegalCheckUpException
import com.carservice.models.requests.CheckUpRequest
import com.carservice.models.views.CheckUpView
import com.carservice.repositories.CarRepository
import com.carservice.repositories.CheckUpRepository
import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * [CheckUpService] manages check-up-related requests
 */
@Service
class CheckUpService(
    private val carRepository: CarRepository,
    private val checkUpRepository: CheckUpRepository
) {

    companion object : KLogging()

    /**
     * Saves given [CheckUpRequest]
     *
     * @return generated check-up ID
     * @throws [IllegalCheckUpException], [CarNotFoundException]
     */
    @Transactional
    fun save(checkUpRequest: CheckUpRequest): Long {
        if (checkUpRequest.price < 0)
            throw IllegalCheckUpException(IllegalCheckUp.PRICE_NON_NEGATIVE.errorMessage)

        val carCheckUp = checkUpRequest.toCheckUp { carId ->
            carRepository.findById(carId) ?: throw CarNotFoundException(carId)
        }

        logger.info { "Saving check-up" }
        val checkUp = checkUpRepository.save(carCheckUp)
        logger.info { "Saved check-up was generated with ID ${checkUp.id}" }

        return checkUp.id
    }

    /**
     * Finds [CheckUp] given its [id]
     *
     * @param[id] check-up ID to be searched
     * @return single [CheckUpView]
     * @throws [CheckUpNotFoundException]
     */
    @Transactional(readOnly = true)
    fun findCheckUp(id: Long): CheckUpView {
        logger.info { "Searching for check-up with ID $id" }
        return checkUpRepository.findById(id)?.let {
            CheckUpView(it, it.car.id)
        } ?: throw CheckUpNotFoundException(id)
    }

    /**
     * Finds all [CheckUp] for a given [Car.id]
     *
     * @param[carId] Car ID
     * @param[pageable] pagination and sorting criteria
     * @return [Page] containing (paged) [CheckUpView] for a given [carId]
     * @throws [CarNotFoundException]
     */
    @Transactional(readOnly = true)
    fun findAllCarCheckUpsByCarId(carId: Long, pageable: Pageable): Page<CheckUpView> {
        logger.info {
            """
            Fetching check-ups for car with ID $carId
            Size: ${pageable.pageSize}
            Page: ${pageable.pageNumber}
            """.trimIndent()
        }
        if (carRepository.existsById(carId).not())
            throw CarNotFoundException(carId)
        return checkUpRepository.findAllByCarId(carId, pageable).map {
            CheckUpView(it, carId)
        }
    }

    /**
     * Deletes single [CheckUp] entity given its [id]
     *
     * @param[id] check-up ID
     * @throws [CheckUpNotFoundException]
     */
    @Transactional
    fun deleteById(id: Long) {
        if (checkUpRepository.existsById(id).not())
            throw CheckUpNotFoundException(id)
        logger.info { "Deleting check-up with ID $id" }
        checkUpRepository.deleteById(id)
        logger.info { "Successfully deleted check-up with ID $id" }
    }

    /**
     * Finds [CheckUp] that were performed last
     *
     * @param[limit] limits number of check-ups to fetch
     * @return [List] of [CheckUpView] that were performed last
     */
    @Transactional(readOnly = true)
    fun lastPerformedCheckUps(limit: Long): List<CheckUpView> {
        logger.info { "Fetching $limit last performed check-ups" }
        return checkUpRepository.lastPerformedCheckUps(limit).map {
            CheckUpView(it, it.car.id)
        }
    }

    /**
     * Finds upcoming [CheckUp]
     *
     * @param[dateTo] puts an upper date limit
     * @return [List] of upcoming [CheckUpView] up to given [LocalDate]
     */
    @Transactional(readOnly = true)
    fun upcomingCheckUps(dateTo: LocalDate): List<CheckUpView> {
        logger.info { "Fetching check-ups up to date $dateTo" }
        return checkUpRepository.upcomingCheckUps(dateTo).map {
            CheckUpView(it, it.car.id)
        }
    }
}
