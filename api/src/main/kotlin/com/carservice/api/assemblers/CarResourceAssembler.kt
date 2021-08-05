package com.carservice.api.assemblers

import com.carservice.api.CarCheckUpController
import com.carservice.api.CarController
import com.carservice.models.resources.CarResource
import com.carservice.models.views.CarView
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

/**
 * Resource Assembler that connects cars to their check-ups
 */
@Component
class CarResourceAssembler : RepresentationModelAssemblerSupport<CarView, CarResource>(
    CarController::class.java, CarResource::class.java
) {

    @Suppress("MagicNumber")
    override fun toModel(entity: CarView): CarResource =
        createModelWithId(entity.id, entity).apply {
            add(
                linkTo<CarCheckUpController> {
                    findAllCarCheckUps(id, Pageable.ofSize(10))
                }.withRel("checkups")
            )
        }

    override fun instantiateModel(entity: CarView): CarResource =
        CarResource(
            entity.id,
            entity.ownerId,
            entity.dateCreated,
            entity.manufacturer,
            entity.model,
            entity.productionYear,
            entity.serialNumber
        )
}
