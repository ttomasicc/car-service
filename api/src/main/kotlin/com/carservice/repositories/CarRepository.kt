package com.carservice.repositories

import com.carservice.domain.Car
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.Repository

/**
 * [CarRepository] interface extends Spring Data JPA [Repository] interface
 * and operates on public.car table
 */
interface CarRepository : Repository<Car, Long> {

    fun save(car: Car): Car

    fun findAll(pageable: Pageable): Page<Car>

    fun findById(id: Long): Car?

    fun existsById(id: Long): Boolean

    fun existsBySerialNumber(serialNumber: String): Boolean

    fun deleteById(id: Long)
}
