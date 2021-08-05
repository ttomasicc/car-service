package com.carservice.domain

import java.io.Serializable
import java.time.LocalDate
import java.time.Year
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * [Car] [Entity] maps to public.car table
 */
@Entity
@Table
class Car(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val ownerId: Long,
    val dateCreated: LocalDate = LocalDate.now(),
    val productionYear: Year,
    val serialNumber: String,
    @ManyToOne
    val name: CarName
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
