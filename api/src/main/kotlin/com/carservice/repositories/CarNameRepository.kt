package com.carservice.repositories

import com.carservice.domain.CarName
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

/**
 * [CarNameRepository] interface extends Spring Data JPA [Repository] interface
 * and operates on public.car_name table
 */
interface CarNameRepository : Repository<CarName, Long> {

    @Modifying
    @Query(
        value = "INSERT INTO car_name VALUES (DEFAULT, :manufacturer, :model) ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    fun upsert(manufacturer: String, model: String)

    // puts the requested car name to cache if valid (not null)
    @Cacheable(value = ["car-name"], unless = "#result == null")
    fun findByManufacturerIgnoreCaseAndModelIgnoreCase(manufacturer: String, model: String): CarName?

    @Query(
        value = "SELECT * FROM car_name WHERE id IN (SELECT DISTINCT name_id FROM car)",
        nativeQuery = true
    )
    fun findUsedCarNames(): List<CarName>

    fun findAll(pageable: Pageable): Page<CarName>
}
