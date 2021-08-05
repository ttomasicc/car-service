package com.carservice.models.resources

import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.LocalDateTime

/**
 * [CheckUpResource] acts as a representation model
 * that will be enriched with hypermedia information and affordances
 *
 * @property[id] Check-up ID
 * @property[dateTime] Date and time when the check-up was performed
 * @property[worker] Worker's name
 * @property[price] Cost of the check-up service
 */
@Relation(collectionRelation = IanaLinkRelations.ITEM_VALUE)
data class CheckUpResource(
    val id: Long,
    val dateTime: LocalDateTime,
    val worker: String,
    val price: Double
) : RepresentationModel<CheckUpResource>()
