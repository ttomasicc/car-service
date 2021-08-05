package com.carservice.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * [CheckUp] [Entity] maps to public.car_check_up table
 */
@Entity
@Table(name = "car_check_up")
class CheckUp(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val dateTime: LocalDateTime,
    val worker: String,
    val price: Double,
    @ManyToOne
    val car: Car
) {

    fun copy(id: Long) =
        CheckUp(id, dateTime, worker, price, car)
}
