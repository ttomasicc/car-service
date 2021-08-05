package com.carservice.api.assemblers

import com.carservice.api.CarCheckUpController
import com.carservice.api.CarController
import com.carservice.api.CheckUpController
import com.carservice.models.resources.CheckUpResource
import com.carservice.models.views.CheckUpView
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

/**
 * Resource Assembler that connects check-ups to cars
 */
@Component
class CarCheckUpResourceAssembler : RepresentationModelAssemblerSupport<CheckUpView, CheckUpResource>(
    CarCheckUpController::class.java, CheckUpResource::class.java
) {

    override fun toModel(entity: CheckUpView): CheckUpResource =
        createModelWithId(entity.id, entity).apply {
            removeLinks()
            add(
                linkTo<CheckUpController> {
                    findCheckUp(id)
                }.withSelfRel(),
                linkTo<CarController> {
                    findCar(entity.carId)
                }.withRel("car")
            )
        }

    override fun instantiateModel(entity: CheckUpView): CheckUpResource =
        CheckUpResource(
            entity.id,
            entity.dateTime,
            entity.worker,
            entity.price
        )
}
