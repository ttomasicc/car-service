package com.carservice.models.external

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This data class maps the attributes needed
 * from the Car Name API response
 *
 * @property[data] contains the list of <Manufacturer, Model> pairs
 */
data class CarNameAPIResponse(
    @JsonProperty("data")
    val data: List<CarNameDescription>
)

data class CarNameDescription(
    @JsonProperty("manufacturer")
    val manufacturer: String,
    @JsonProperty("model_name")
    val model: String
)
