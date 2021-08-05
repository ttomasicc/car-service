package com.carservice.tasks

import com.carservice.models.external.CarNameAPIResponse
import com.carservice.repositories.CarNameRepository
import mu.KLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.cache.annotation.CacheEvict
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Scheduling service updates the car names table every day at midnight (Europe/Zagreb timezone)
 */
@Service
class CarNameSchedulingService(
    private val webClient: WebClient,
    private val carNameRepository: CarNameRepository
) {

    companion object : KLogging()

    // every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    // safety: if communication between multiple application instances breaks for a couple of minutes
    @SchedulerLock(name = "Updating public.car_name table", lockAtLeastFor = "5m")
    // safety: default isolation is probably enough
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    // deletes all cached car names upon updated
    @CacheEvict(value = ["car-name"], allEntries = true)
    fun updateCarNames() {
        logger.info { "Updating car names in the database" }
        fetchCarNames().data.forEach {
            carNameRepository.upsert(it.manufacturer, it.model)
        }
    }

    @Suppress("TooGenericExceptionThrown")
    fun fetchCarNames(): CarNameAPIResponse =
        webClient
            .get()
            .uri("/api/v1/cars.json")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<CarNameAPIResponse>()
            .block() ?: throw RuntimeException("Failed to fetch car names")
}
