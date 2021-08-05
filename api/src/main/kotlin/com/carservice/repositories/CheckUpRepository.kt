package com.carservice.repositories

import com.carservice.domain.CheckUp
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import java.time.LocalDate

/**
 * [CheckUpRepository] interface extends Spring Data JPA [Repository] interface
 * and operates on public.car_check_up table
 */
interface CheckUpRepository : Repository<CheckUp, Long> {

    fun save(checkUp: CheckUp): CheckUp

    fun existsById(id: Long): Boolean

    fun findById(id: Long): CheckUp?

    fun findAllByCarId(carId: Long, pageable: Pageable): Page<CheckUp>

    @Query(
        value = "SELECT * FROM car_check_up WHERE date_time <= CURRENT_DATE ORDER BY date_time DESC LIMIT :limit",
        nativeQuery = true
    )
    fun lastPerformedCheckUps(limit: Long): List<CheckUp>

    @Query(
        value = "SELECT * FROM car_check_up WHERE date_time BETWEEN CURRENT_DATE AND :dateTo ORDER BY date_time",
        nativeQuery = true
    )
    fun upcomingCheckUps(dateTo: LocalDate): List<CheckUp>

    fun deleteById(id: Long)
}
