package com.carservice.domain

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * [CarName] [Entity] maps to public.car_name table
 */
@Entity
@Table
class CarName(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val manufacturer: String,
    val model: String
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
