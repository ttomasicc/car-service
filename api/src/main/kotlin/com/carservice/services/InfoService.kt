package com.carservice.services

import com.carservice.domain.CarName
import com.carservice.models.views.CarNameView
import com.carservice.repositories.CarNameRepository
import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * [InfoService] manages general information
 */
@Service
class InfoService(
    private val carNameRepository: CarNameRepository
) {

    companion object : KLogging()

    /**
     * Finds all valid [CarName]
     *
     * @param[pageable] pagination and sorting criteria
     * @return [Page] of available [CarNameView]
     */
    @Transactional(readOnly = true)
    fun findAllCarNames(pageable: Pageable): Page<CarNameView> {
        logger.info { "Fetching all car names" }
        return carNameRepository.findAll(pageable).map {
            CarNameView(it)
        }
    }

    /**
     * Finds used [CarName]
     *
     * @return [List] of used [CarNameView]
     */
    @Transactional(readOnly = true)
    fun findUsedCarNames(): List<CarNameView> {
        logger.info { "Fetching used car names" }
        return carNameRepository.findUsedCarNames().map {
            CarNameView(it)
        }
    }
}
