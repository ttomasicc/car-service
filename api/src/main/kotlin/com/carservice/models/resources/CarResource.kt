package com.carservice.models.resources

import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.LocalDate
import java.time.Year

/**
 * [CarResource] acts as a representation model
 * that will be enriched with hypermedia information and affordances
 *
 * @property[id] Car ID
 * @property[ownerId] Owner ID
 * @property[dateCreated] Date when the car was added to the database
 * @property[manufacturer] Manufacturer name
 * @property[model] Model name
 * @property[productionYear] The year the car was produced
 * @property[serialNumber] Unique car serial number
 */
@Relation(collectionRelation = IanaLinkRelations.ITEM_VALUE)
data class CarResource(
    val id: Long,
    val ownerId: Long,
    val dateCreated: LocalDate,
    val manufacturer: String,
    val model: String,
    val productionYear: Year,
    val serialNumber: String,
) : RepresentationModel<CarResource>()
